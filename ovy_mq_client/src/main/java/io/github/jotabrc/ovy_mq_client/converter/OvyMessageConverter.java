package io.github.jotabrc.ovy_mq_client.converter;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.converter.ContentTypeResolver;
import org.springframework.util.MimeType;

import java.util.Collection;
import java.util.List;

public class OvyMessageConverter extends AbstractMessageConverter {

    protected OvyMessageConverter(MimeType supportedMimeType) {
        super(supportedMimeType);
    }

    protected OvyMessageConverter(MimeType... supportedMimeTypes) {
        super(supportedMimeTypes);
    }

    protected OvyMessageConverter(Collection<MimeType> supportedMimeTypes) {
        super(supportedMimeTypes);
    }

    @Override
    public List<MimeType> getSupportedMimeTypes() {
        return super.getSupportedMimeTypes();
    }

    @Override
    protected void addSupportedMimeTypes(MimeType... supportedMimeTypes) {
        super.addSupportedMimeTypes(supportedMimeTypes);
    }

    @Override
    public void setContentTypeResolver(ContentTypeResolver resolver) {
        super.setContentTypeResolver(resolver);
    }

    @Override
    public ContentTypeResolver getContentTypeResolver() {
        return super.getContentTypeResolver();
    }

    @Override
    public void setStrictContentTypeMatch(boolean strictContentTypeMatch) {
        super.setStrictContentTypeMatch(strictContentTypeMatch);
    }

    @Override
    public boolean isStrictContentTypeMatch() {
        return super.isStrictContentTypeMatch();
    }

    @Override
    public void setSerializedPayloadClass(Class<?> payloadClass) {
        super.setSerializedPayloadClass(payloadClass);
    }

    @Override
    public Class<?> getSerializedPayloadClass() {
        return super.getSerializedPayloadClass();
    }

    @Override
    protected boolean canConvertFrom(Message<?> message, Class<?> targetClass) {
        return super.canConvertFrom(message, targetClass);
    }

    @Override
    protected boolean canConvertTo(Object payload, MessageHeaders headers) {
        return super.canConvertTo(payload, headers);
    }

    @Override
    protected boolean supportsMimeType(MessageHeaders headers) {
        return super.supportsMimeType(headers);
    }

    @Override
    protected MimeType getMimeType(MessageHeaders headers) {
        return super.getMimeType(headers);
    }

    @Override
    protected MimeType getDefaultContentType(Object payload) {
        return super.getDefaultContentType(payload);
    }

    @Override
    protected Object convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
        return super.convertFromInternal(message, targetClass, conversionHint);
    }

    @Override
    protected Object convertToInternal(Object payload, MessageHeaders headers, Object conversionHint) {
        return super.convertToInternal(payload, headers, conversionHint);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return false;
    }
}
