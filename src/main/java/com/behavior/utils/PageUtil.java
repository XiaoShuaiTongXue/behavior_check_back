package com.behavior.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageUtil {

    public static Pageable getPageable(int page,int size,Sort sort){
        page = page < Constants.Page.DEFAULT_PAGE ? Constants.Page.DEFAULT_PAGE : page;
        size = size < Constants.Page.DEFAULT_SIZE ? Constants.Page.DEFAULT_SIZE : size;
        if (sort == null){
            return PageRequest.of(page - 1, size);
        }
        return PageRequest.of(page - 1, size, sort);
    }
}
