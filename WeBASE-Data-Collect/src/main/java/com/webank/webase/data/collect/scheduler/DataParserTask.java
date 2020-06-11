/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.webank.webase.data.collect.scheduler;

import com.webank.webase.data.collect.base.enums.DataStatus;
import com.webank.webase.data.collect.base.properties.ConstantProperties;
import com.webank.webase.data.collect.group.GroupService;
import com.webank.webase.data.collect.group.entity.TbGroup;
import com.webank.webase.data.collect.parser.ParserService;
import com.webank.webase.data.collect.receipt.ReceiptService;
import com.webank.webase.data.collect.receipt.entity.TbReceipt;
import com.webank.webase.data.collect.transaction.TransactionService;
import com.webank.webase.data.collect.transaction.entity.TbTransaction;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * data parser
 * 
 */
@Log4j2
@Component
public class DataParserTask {

    @Autowired
    private GroupService groupService;
    @Autowired
    private ParserService parserService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private ConstantProperties cProperties;

    @Scheduled(fixedDelayString = "${constant.dataParserTaskFixedDelay}", initialDelay = 1000)
    public void taskStart() {
        parserStart();
    }

    /**
     * parserStart.
     */
    public void parserStart() {
        log.info("start parser.");
        Instant startTime = Instant.now();
        List<TbGroup> groupList = groupService.getGroupList(null, DataStatus.NORMAL.getValue());
        if (CollectionUtils.isEmpty(groupList)) {
            log.warn("parser jump over: not found any group");
            return;
        }
        // count down group, make sure all group's transMonitor finished
        CountDownLatch latch = new CountDownLatch(groupList.size());
        groupList.stream()
                .forEach(group -> parserProcess(latch, group.getChainId(), group.getGroupId()));
        try {
            latch.await();
        } catch (InterruptedException ex) {
            log.error("InterruptedException", ex);
            Thread.currentThread().interrupt();
        }
        log.info("end parser useTime:{} ", Duration.between(startTime, Instant.now()).toMillis());
    }

    @Async("asyncExecutor")
    private void parserProcess(CountDownLatch latch, int chainId, int groupId) {
        log.info("start parserProcess. chainId:{} groupId:{}", chainId, groupId);
        try {
            Instant startTimem = Instant.now();
            Long useTimeSum = 0L;
            do {
                List<TbTransaction> transList =
                        transactionService.qureyUnStatTransactionList(chainId, groupId);
                if (CollectionUtils.isEmpty(transList)) {
                    return;
                }
                // parser
                for (TbTransaction tbTransaction : transList) {
                    parserTransaction(chainId, groupId, tbTransaction);
                }

                // parser useTime
                useTimeSum = Duration.between(startTimem, Instant.now()).getSeconds();
            } while (useTimeSum < cProperties.getDataParserTaskFixedDelay());
        } catch (Exception ex) {
            log.error("fail parserProcess chainId:{} groupId:{} ", chainId, groupId, ex);
        } finally {
            if (Objects.nonNull(latch)) {
                latch.countDown();
            }
        }
        log.info("end parserProcess. chainId:{} groupId:{}", chainId, groupId);
    }

    private void parserTransaction(int chainId, int groupId, TbTransaction tbTransaction) {
        try {
            TbReceipt tbReceipt = receiptService.getTbReceiptByHash(chainId, groupId,
                    tbTransaction.getTransHash());
            if (ObjectUtils.isEmpty(tbTransaction) || ObjectUtils.isEmpty(tbReceipt)) {
                return;
            }
            parserService.parserTransaction(chainId, groupId, tbTransaction, tbReceipt);
        } catch (Exception ex) {
            log.error("fail parserTransaction chainId:{} groupId:{} ", chainId, groupId, ex);
        }
    }
}
