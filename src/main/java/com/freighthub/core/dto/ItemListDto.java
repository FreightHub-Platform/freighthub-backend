package com.freighthub.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ItemListDto implements Serializable {

    private List<ItemDto> items;

}
