package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatSeckillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Spring MVC Controller
 * Created by chen on 2017/6/9.
 */
@Controller
@RequestMapping(value = "/seckill") //url格式：/模块/资源/{id}/细分--->/seckill/list/
public class SeckillController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {

		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list",list);
		//list.jsp + model = ModelAndView
		return "list"; //WEB-INF/jsp/list.jsp
	}

	@RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model){
		if(seckillId == null){
			return "redirect:/seckill/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		if(seckill == null){
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill",seckill);
		return "detail";
	}

	@RequestMapping(value = "/{seckillId}/exposer",
					method = RequestMethod.POST,
					produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable Long seckillId){
		SeckillResult<Exposer> result;
		try {
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(true,exposer);
		} catch (Exception e){
			logger.error(e.getMessage(),e);
			result = new SeckillResult<Exposer>(false,e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/{seckillId}/{md5}/execution",
					method = RequestMethod.POST,
					produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
												   @CookieValue(value = "killPhone", required = false) Long userPhone,
												   @PathVariable("md5") String md5){

		//Spring MVC validate
		if (userPhone == null){
			return new SeckillResult<SeckillExecution>(false,"用户未注册");
		}

		try {
			//存储过程调用
			SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(seckillId,userPhone,md5);
			return new SeckillResult<SeckillExecution>(true,seckillExecution);
		} catch (RepeatSeckillException e){
			SeckillExecution seckillExecution = new SeckillExecution(seckillId,SeckillStateEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExecution>(true,seckillExecution);
		} catch (SeckillCloseException e){
			SeckillExecution seckillExecution = new SeckillExecution(seckillId,SeckillStateEnum.END);
			return new SeckillResult<SeckillExecution>(true,seckillExecution);
		} catch (Exception e){
			SeckillExecution seckillExecution = new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
			return new SeckillResult<SeckillExecution>(true,seckillExecution);
		}
	}

	@RequestMapping(value = "/time/now", method = RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time(){
		Date nowTime = new Date();
		logger.info("nowTime={}",nowTime.getTime());
		return new SeckillResult<Long>(true,nowTime.getTime());
	}
}
