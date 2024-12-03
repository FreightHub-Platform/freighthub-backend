package com.freighthub.core.service;

import com.freighthub.core.dto.CostFunctionDto;
import com.freighthub.core.entity.CostFunction;
import com.freighthub.core.repository.CostFunctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CostFunctionService {

    @Autowired
    private CostFunctionRepository costFunctionRepository;

    @Transactional
    public CostFunction getFunction() {
        return costFunctionRepository.findAll().getFirst();
    }

    @Transactional
    public CostFunction updateFunction(CostFunctionDto cost) {
        CostFunction costFunction = costFunctionRepository.findAll().getFirst();
        costFunction.setDieselPrice(cost.getDieselPrice());
        costFunction.setFixedCost(cost.getFixedCost());
        costFunction.setDriverWage(cost.getDriverWage());
        return costFunctionRepository.save(costFunction);
    }
}
