package com.devk.training.camunda.Payment.CheckCredit;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CheckCreditHandlerTest {

    @Mock
    CheckCreditService checkCreditService;

    @BeforeAll
    void init(){
//        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testIfFailureIsWorking() {
        ExternalTask externalTaskMock = Mockito.mock(ExternalTask.class);
        when(externalTaskMock.getVariable("shouldFail")).thenReturn(true);
        when(externalTaskMock.getRetries()).thenReturn(null);

        ExternalTaskService externalTaskServiceMock = Mockito.mock(ExternalTaskService.class);
        CheckCreditHandler checkCreditHandler = new CheckCreditHandler(checkCreditService);
        checkCreditHandler.execute(externalTaskMock, externalTaskServiceMock);
        Mockito.verify(externalTaskServiceMock).handleFailure(eq(externalTaskMock), anyString(), anyString(), eq(3), anyLong());
        Mockito.verify( externalTaskServiceMock, never()).complete(any(externalTaskMock.getClass()), anyMap());
    }
    
}