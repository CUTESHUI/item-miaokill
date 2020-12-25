package com.shui.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ToString
public class KillDto implements Serializable{

    @NotNull
    private Integer killId;
    private Integer userId;
}