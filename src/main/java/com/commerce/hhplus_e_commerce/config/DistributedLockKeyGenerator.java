package com.commerce.hhplus_e_commerce.config;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class DistributedLockKeyGenerator {

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    public String generate(MethodSignature signature,Object[] args,String key){
        String methodName = signature.getMethod().getName();
        String parserKey = parseSpEL(signature,args,key);

        return methodName+":"+parserKey;
    }

    private String parseSpEL(MethodSignature signature,Object[] args,String key){
        String[] parameterNames = signature.getParameterNames();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i=0;i<args.length;i++){
            context.setVariable(parameterNames[i], args[i]);
        }

        return expressionParser.parseExpression(key).getValue(context,String.class);
    }

}
