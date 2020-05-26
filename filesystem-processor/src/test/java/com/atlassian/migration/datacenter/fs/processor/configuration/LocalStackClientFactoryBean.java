package com.atlassian.migration.datacenter.fs.processor.configuration;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsAsyncClientBuilder;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.cloud.aws.core.config.AmazonWebserviceClientFactoryBean;
import org.springframework.cloud.aws.core.region.RegionProvider;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

public class LocalStackClientFactoryBean<T extends AmazonWebServiceClient> extends AmazonWebserviceClientFactoryBean<T> {

    private final String endpointURL;
    private final Class<? extends AmazonWebServiceClient> clientClass;
    private final AWSCredentialsProvider credentialsProvider;
    private RegionProvider regionProvider;
    private Region customRegion;
    private ExecutorService executor;

    public LocalStackClientFactoryBean(Class clientClass, AWSCredentialsProvider credentialsProvider, String endpointURL) {
        super(clientClass, credentialsProvider);
        this.endpointURL = endpointURL;
        this.clientClass = clientClass;
        this.credentialsProvider = credentialsProvider;
    }

    public LocalStackClientFactoryBean(Class clientClass, AWSCredentialsProvider credentialsProvider, RegionProvider regionProvider, String endpointURL) {
        super(clientClass, credentialsProvider, regionProvider);
        this.endpointURL = endpointURL;
        this.clientClass = clientClass;
        this.credentialsProvider = credentialsProvider;
        this.regionProvider = regionProvider;
    }

    @Override
    public T createInstance() throws Exception {
        String builderName = this.clientClass.getName() + "Builder";
        Class<?> className = ClassUtils.resolveClassName(builderName, ClassUtils.getDefaultClassLoader());
        Method method = ClassUtils.getStaticMethod(className, "standard");
        Assert.notNull(method, "Could not find standard() method in class:'" + className.getName() + "'");
        AwsClientBuilder<?, T> builder = (AwsClientBuilder) ReflectionUtils.invokeMethod(method, null);
        if (this.executor != null) {
            AwsAsyncClientBuilder<?, T> asyncBuilder = (AwsAsyncClientBuilder) builder;
            asyncBuilder.withExecutorFactory(() -> this.executor);
        }

        if (this.credentialsProvider != null) {
            builder.withCredentials(this.credentialsProvider);
        }

        if (builder.getClass() == AmazonS3ClientBuilder.class) {
            Method pathStyleMethod = builder.getClass().getMethod("withPathStyleAccessEnabled", Boolean.class);
            pathStyleMethod.invoke(builder, true);
        }

        if (this.customRegion != null) {
            AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(this.endpointURL, this.customRegion.getName());
            builder.withEndpointConfiguration(endpointConfiguration);
        } else if (this.regionProvider != null) {
            AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(this.endpointURL, this.regionProvider.getRegion().getName());
            builder.withEndpointConfiguration(endpointConfiguration);
        } else {
            AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(this.endpointURL, Regions.DEFAULT_REGION.getName());
            builder.withEndpointConfiguration(endpointConfiguration);
        }

        return builder.build();
    }
}
