package cv.beriholic.beeyes.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.ContextHolder;
import cv.beriholic.beeyes.exception.AbstractBeEyesException;
import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.models.dto.Context;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.utils.JsonUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {
    }

    @Pointcut("execution(* *.*(..))")
    public void allMethods() {
    }

    @Around("controllerPointcut() && allMethods()")
    public Object serviceMethodHandler(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        if (thisJoinPoint.getSignature() instanceof MethodSignature signature) {
            Method currentMethod = thisJoinPoint.getTarget().getClass().getMethod(signature.getName(), signature.getParameterTypes());
            Object[] args = thisJoinPoint.getArgs();
            try {
                fillContext(currentMethod);
                Object proceed = thisJoinPoint.proceed();
                LOGGER.info("调用日志【方法】:{};【参数】:{},【结果】:{}", currentMethod.getName(), JsonUtil.toJSONString(args), JsonUtil.toJSONString(proceed));
                return proceed;
            } catch (ConstraintViolationException e) {
                String violationMessage = e.getConstraintViolations().stream().map(ConstraintViolation::getMessageTemplate).toList().getFirst();
                LOGGER.warn("参数异常【方法】:{};【参数】:{}", currentMethod.getName(), JsonUtil.toJSONString(args), e);
                return RestBean.failed(ErrorCode.PARAM_INVALID.getCode(), violationMessage);
            } catch (AbstractBeEyesException ex) {
                LOGGER.warn("业务异常【方法】:{};【参数】:{}", currentMethod.getName(), JsonUtil.toJSONString(args), ex);
                return RestBean.failed(ex.getCode(), ex.getMessage());
            } catch (Exception e) {
                LOGGER.error("系统异常【方法】:{};【参数】:{}", currentMethod.getName(), JsonUtil.toJSONString(args), e);
                return RestBean.failed(ErrorCode.SYSTEM_ERROR);
            } finally {
                ContextHolder.clear();
            }
        }
        return thisJoinPoint.proceed();

    }

    private void fillContext(Method currentMethod) {
        if (!needFillOperationContext(currentMethod) || !StpUtil.isLogin()) {
            return;
        }
        Long userId = StpUtil.getLoginIdAsLong();
        Context context = new Context();
        context.setUserId(userId);
        ContextHolder.setCurrent(context);
    }

    private boolean needFillOperationContext(Method currentMethod) {
        IgnoreContextFill ignoreContextFill = currentMethod.getAnnotation(IgnoreContextFill.class);
        return ignoreContextFill == null;
    }
}
