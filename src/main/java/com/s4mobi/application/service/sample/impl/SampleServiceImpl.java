package com.s4mobi.application.service.sample.impl;

import com.s4mobi.application.entity.SampleEntity;
import com.s4mobi.application.exception.BusinessException;
import com.s4mobi.application.host.BaseEndpoint;
import com.s4mobi.application.service.sample.SampleService;
import com.s4mobi.infrastructure.model.ParsePageable;
import com.s4mobi.infrastructure.network.SampleFeignClient;
import com.s4mobi.infrastructure.network.dto.response.ParseResults;
import com.s4mobi.infrastructure.network.dto.response.ParseSampleResponse;
import com.s4mobi.infrastructure.utils.ParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SampleServiceImpl implements SampleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseEndpoint.class);

    @Autowired
    private SampleFeignClient sampleFeignClient;

    @Override
    public Page<SampleEntity> getSamples(final String search, final Pageable pageable) {
        try {
            String where = search;
            if (search != null) {
                where = ParseUtils.parseSearch(search, SampleEntity.class);
            }
            final ParsePageable pagination = ParseUtils.parsePageable(pageable);

            LOGGER.info("[GET] Calling Parse to get all Samples - /Sample");
            final ParseResults<ParseSampleResponse> samples = sampleFeignClient.getSamples(where, pagination.limit, pagination.skip, pagination.orders);
            LOGGER.info("[GET] Parse returned the object: {} - /Sample", this.getJsonResponse(samples));

            final List<SampleEntity> entities = samples.getResults().stream().map(SampleEntity::fromResponse).collect(Collectors.toList());
            return new PageImpl<>(entities, pageable, samples.getCount());
        } catch (Exception e) {
            LOGGER.error("Error at get all Samples in Parse: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public SampleEntity getSample(final String id) {
        try {
            LOGGER.info("[GET] Calling Parse to get one Sample - /Sample/{}", id);
            final ParseSampleResponse sample = sampleFeignClient.getSample(id);
            LOGGER.info("[GET] Parse returned the object: {} - /Sample/{}", this.getJsonResponse(sample), id);
            return SampleEntity.fromResponse(sample);
        } catch (Exception e) {
            LOGGER.error("Error at get all Samples in Parse: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public SampleEntity createSample(final SampleEntity entity) {
        try {
            LOGGER.info("[POST] Calling Parse to create one Sample - /Sample");
            final ParseSampleResponse sample = sampleFeignClient.postSample(entity.toRequest());
            LOGGER.info("[POST] Parse returned id: {} and creation date: {} - /Sample", sample.getId(), sample.getCreatedAt());
            return SampleEntity.fromResponse(sample);
        } catch (Exception e) {
            LOGGER.error("Error at get all Samples in Parse: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public void updateSample(final String id, final SampleEntity entity) {
        try {
            LOGGER.info("[PUT] Calling Parse to update one Sample - /Sample/{}", id);
            final ParseSampleResponse sample = sampleFeignClient.putSample(id, entity.toRequest());
            LOGGER.info("[PUT] Parse returned change date: {} - /Sample/{}", sample.getUpdatedAt(),id);
        } catch (Exception e) {
            LOGGER.error("Error at get all Samples in Parse: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public void remveSample(final String id) {
        try {
            LOGGER.info("[DELETE] Calling Parse to update one Sample - /Sample/{}", id);
            sampleFeignClient.deleteSample(id);
            LOGGER.info("[PUT] Parse returned success - /Sample/{}", id);
        } catch (Exception e) {
            LOGGER.error("Error at get all Samples in Parse: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }
}
