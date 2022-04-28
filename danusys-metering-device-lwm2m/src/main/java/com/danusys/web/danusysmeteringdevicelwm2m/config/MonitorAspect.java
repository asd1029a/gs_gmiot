package com.danusys.web.danusysmeteringdevicelwm2m.config;

import com.danusys.web.danusysmeteringdevicelwm2m.service.DeviceRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MonitorAspect {
//    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
//    private static final Logger log = LoggerFactory.getLogger("FILE_LOGGER");
//    private ObjectMapper mapper = new ObjectMapper();

//    private String host = "";
//    private String ip = "";
//    private String clientIp = "";
//    private String clientUrl = "";
    private final DeviceRegisterService deviceRegisterService;

    @PostConstruct
    public void init() throws UnknownHostException {
//        InetAddress addr = InetAddress.getLocalHost();
//        this.host = addr.getHostName();
//        this.ip = addr.getHostAddress();

    }

//    @Around("execution(* com.danusys.web.*.*.*Controller.*(..))")
//    public Object controllerAroundLogging(ProceedingJoinPoint pjp) throws Throwable {
//        String timeStamp = new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Timestamp(System.currentTimeMillis()));
//
//        //실행전
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//
//        HttpServletRequest request = attributes.getRequest();
//        this.clientIp = request.getRemoteAddr();
//        this.clientUrl = request.getRequestURL().toString();
//        String callFunction = pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
//
//        LogInfo loginfo = new LogInfo();
//        loginfo.setTimestamp(timeStamp);
//        loginfo.setHostname(host);
//        loginfo.setHostIp(ip);
//        loginfo.setClientIp(clientIp);
//        loginfo.setClientUrl(clientUrl);
//        loginfo.setCallFunction(callFunction);
//        loginfo.setType("CONTROLLER_REQ");
//        loginfo.setParameter(mapper.writeValueAsString(request.getParameterMap()));
//
//        long beforeTimeMillis = System.currentTimeMillis();

//        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>controllerAroundLogging");
//        log.info(mapper.writeValueAsString(loginfo));
//        System.out.println(">>> 실행시작 : "
//            + pjp.getSignature().getDeclaringTypeName() + "."
//            + pjp.getSignature().getName());
        //System.out.println(mapper.writeValueAsString(loginfo));

//        Object result = pjp.proceed();
//
//        //실행후
//        timeStamp = new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Timestamp(System.currentTimeMillis()));
//
//        loginfo.setTimestamp(timeStamp);
//        loginfo.setType("CONTROLLER_RES");
//        loginfo.setParameter(mapper.writeValueAsString(result));
//
//        long afterTimeMillis = System.currentTimeMillis() - beforeTimeMillis;
//
//        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        log.info(mapper.writeValueAsString(loginfo));
//        System.out.println(">>> 실행끝 : " + afterTimeMillis + " 밀리초 소요 "
//            + pjp.getSignature().getDeclaringTypeName() + "."
//            + pjp.getSignature().getName());
        //System.out.println(mapper.writeValueAsString(loginfo));

//        return result;
//    }


//    @AfterThrowing(pointcut="execution(* com.danusys.web.*.*.*.*(..))" ,throwing="ex")
//    public void allThrowingLogging(Throwable ex) {
//        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>allThrowingLogging");
//        String timeStamp = new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Timestamp(System.currentTimeMillis()));
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//
////        System.out.println(">>> throwing >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        Map<String, Object> throwMap = new HashMap<String, Object>();
//        //throwMap.put("serverIp",ip);
//        //throwMap.put("serverHost",host);
//        throwMap.put("exceptionContent",ex.toString());
//        throwMap.put("requestIp",request.getRemoteAddr());
//        throwMap.put("requestUrl",request.getRequestURL().toString());
//
////        System.out.println(throwMap.toString());
//        log.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        log.error(throwMap.toString());

//        System.out.println(">>> throwing >>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        /////db table (t_exception)
        ///insert 추후예정

//    }

    @AfterReturning(pointcut = "execution(* com.danusys.web.*.*.*Controller.postObserveDataNotice(..))", returning = "returnObj")
    public void allReturningLogging(JoinPoint jp, Object returnObj) throws Throwable {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>allReturningLogging");
        System.out.println(">>> returning >>>>>>>>>>>>>>>>>");
        System.out.println(returnObj);
//        String timeStamp = new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Timestamp(System.currentTimeMillis()));

  /*      deviceRegisterService.getDeviceStatusCheck();

        log.info("{}",returnObj);
        System.out.println(jp.getSignature());
 */   }

}
