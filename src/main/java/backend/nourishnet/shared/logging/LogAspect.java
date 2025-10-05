package backend.nourishnet.shared.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {
    @Around("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Service *)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        var log = LoggerFactory.getLogger(pjp.getTarget().getClass());
        var method = pjp.getSignature().getName();
        var args = pjp.getArgs();
        log.info("in {} args={}", method, args);
        try {
            var result = pjp.proceed();
            log.info("out {} result={}", method, result);
            return result;
        } catch (Exception e) {
            log.error("err {} {}", method, e.toString());
            throw e;
        }
    }
}