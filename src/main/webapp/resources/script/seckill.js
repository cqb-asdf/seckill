//存放主要交互逻辑js代码
//通过Json 实现 Javascript的模块化
//1.用户注册登录交互逻辑
var seckill = {
    //封装秒杀相关ajax的url
    URL : {
        now : function () {
            return '/seckill/time/now';
        },
        exposer : function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        killUrl : function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },
    //处理秒杀逻辑
    handleSeckill : function (seckillId,node) {
        //获取秒杀地址，控制实现逻辑，执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        console.log("post");
        $.post(seckill.URL.exposer(seckillId),{},function (result) {
            console.log(result['success']);
            console.log("post end");
            //在回调函数中执行交互流程
            if(result && result['success']){
                var exposer = result['data'];
                if (exposer['exposed']){
                    //开启秒杀
                    //获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.killUrl(seckillId,md5);
                    console.log("killUrl:" + killUrl);
                    //绑定一次点击事件，防止过载请求
                    $( '#killBtn' ).one('click',function () {
                        //执行秒杀请求操作
                        //1:禁用按钮
                        console.log("执行秒杀");
                        $(this).addClass('disabled');
                        //2：发送秒杀请求执行秒杀
                        $.post(killUrl,{},function (result) {
                            console.log("执行秒杀");
                            console.log(result['success']);
                            console.log(result);
                            if (result && result['success']){
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                console.log(stateInfo);
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                    node.show();
                } else {
                    //未开启秒杀
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    //重新计算计时逻辑
                    seckill.countdown(seckillId,now,start,end);
                }
            } else {
                console.log('result:' +result);
            }
        })
    },
    //验证手机号
    validatePhone : function (phone){
        if(phone && phone.length == 11 && !isNaN(phone)){
            return true;
        } else {
            return false;
        }
    },
    countdown : function (seckillId,nowTime,startTime,endTime) {
        var seckillBox = $( '#seckill-box');
        console.log("开始判断秒杀时间");
        //时间判断
        if (nowTime > endTime){
            //秒杀结束
            seckillBox.html( '秒杀已经结束!' );
        } else if ( nowTime < startTime){
            //秒杀未开启，计时事件绑定
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime,function (event) {
                //时间格式
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
                //时间完成后回调事件
            }).on('finish.countdown',function () {
                //获取秒杀地址，控制实现逻辑，执行秒杀
                seckill.handleSeckill(seckillId,seckillBox);
            })
        } else {
            //秒杀已开启
            console.log('秒杀已开启');
            seckill.handleSeckill(seckillId,seckillBox);
        }
    },
    //详情页秒杀逻辑
    detail : {
        //详情页初始化
        init : function (params) {
            //手机验证和登录，计时交互
            //规划我们的交互流程
            //在cookie查找手机号
            console.log("在cookie中获取手机号码");
            var killPhone = $.cookie('killPhone');
            //验证手机号
            if (!seckill.validatePhone(killPhone)) {
                //绑定phone
                //控制输出
                console.log("cookie中无号码，用户还未填写手机号码");
                var killPhoneModal = $('#killPhoneModal');
                //显示弹出层
                killPhoneModal.modal({
                    show: true, //显示弹出层
                    backdrop: 'static', //禁止位置关闭
                    keyboard: false //关闭键盘事件
                });
                console.log("用户输入phone，还未单击Submit按钮");
                $( '#killPhoneBtn' ).click(function () {
                    console.log("用户单击Submit按钮");
                    var inputPhone = $( '#killPhoneKey').val();
                    console.log("获取输入phone");
                    if (seckill.validatePhone(inputPhone)){
                        //电话写入cookie
                        console.log("用户准备输入新的手机号码");
                        $.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'});
                        console.log("用户成功输入新的手机号码，注册成功，并写入cookie");
                        //用户登陆后，重新刷新页面
                        console.log("准备刷新");
                        window.location.reload();
                    } else {
                        $( '#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！</label>').show(300);
                    }
                });
            }
            else {
                console.log("用户已成功注册，无须再注册");
                //已经登录
                //2.计时交互逻辑
                var startTime = params['startTime'];
                var endTime = params['endTime'];
                var seckillId = params['seckillId'];
                console.log('startTime=:' + startTime);
                //获取指定url返回的json对象，并作为参数传给回调函数
                $.get(seckill.URL.now(), {}, function (result) {
                    if (result && result['success']) {
                        var nowTime = result['data'];
                        console.log('拿到当前时间nowTime=:' + nowTime);
                        //秒杀时间判断
                        seckill.countdown(seckillId, nowTime, startTime, endTime);
                    } else {
                        console.log('result' + result);
                    }
                });
            }
        }
    }
}