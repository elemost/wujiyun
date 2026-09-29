package com.wuji.workflow.config;


import com.wuji.workflow.handler.multiinstance.BpmActivityBehaviorFactory;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.common.engine.impl.de.odysseus.el.misc.TypeConverter;
import org.flowable.common.engine.impl.de.odysseus.el.misc.TypeConverterImpl;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.flowable.spring.boot.process.Process;
import org.flowable.validation.ProcessValidatorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * @Description: flowable配置
 */
@Configuration
public class FlowBpmnConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    @Override
    public void configure(SpringProcessEngineConfiguration configuration) {
        configuration.setAsyncExecutorActivate(true);
        configuration.setProcessDefinitionCacheLimit(2000);
        configuration.setActivityBehaviorFactory(new BpmActivityBehaviorFactory());
        //设置自定义的uuid生成策略
        configuration.setIdGenerator(uuidGenerator());
    }

    @Process
    @Bean
    public TaskExecutor processTaskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public ProcessValidatorFactory processValidator(){
        return new ProcessValidatorFactory();
    }

    @Bean
    public TypeConverter typeConverter() {
        return new TypeConverterImpl();
    }

    @Bean
    public UuidGenerator uuidGenerator() {
        return new UuidGenerator();
    }

    /**
     * BpmnXMLConverter
     *
     * @return BpmnXMLConverter
     */
    @Bean
    public BpmnXMLConverter createBpmnXMLConverter() {
        return new BpmnXMLConverter();
    }


    /**
     * 在配置文件中如果没有字段，使用@Value的时候就会忽略掉，不会报错
     *
     * @return
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreUnresolvablePlaceholders(true);
        return configurer;
    }

}
