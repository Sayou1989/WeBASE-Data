package com.webank.webase.data.collect.dao.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class TbEventInfo implements Serializable {

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_event_info.block_number
     *
     */
    private BigInteger blockNumber;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_event_info.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_event_info.contract_name
     *
     * @mbg.generated
     */
    private String contractName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_event_info.contract_address
     *
     * @mbg.generated
     */
    private String contractAddress;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_event_info.user_address
     *
     * @mbg.generated
     */
    private String userAddress;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_event_info.trans_hash
     *
     * @mbg.generated
     */
    private String transHash;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_event_info.block_timestamp
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date blockTimestamp;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_event_info.create_time
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_event_info.modify_time
     *
     * @mbg.generated
     */
    private Date modifyTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_event_info.event_info
     *
     * @mbg.generated
     */
    private String eventInfo;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tb_event_info
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tb_event_info
     *
     * @mbg.generated
     */
    private String dynamicTableName;
}
