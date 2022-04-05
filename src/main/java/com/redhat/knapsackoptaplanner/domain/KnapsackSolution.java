package com.redhat.knapsackoptaplanner.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

//Add PlanningSolution annotation here
@PlanningSolution
public class KnapsackSolution {

    //Add Ingots here
    @PlanningEntityCollectionProperty
    private List<Ingot> ingots;

    //Add Knapsack here
    @ProblemFactProperty
    private Knapsack knapsack;

    //Add selected valuerangeprovider here
    @ValueRangeProvider(id = "selected")
    public CountableValueRange<Boolean> getSelected() {
        return ValueRangeFactory.createBooleanValueRange();
    }
    //Add PlanningScore here
    @PlanningScore
    private HardSoftScore score;

    public KnapsackSolution() {
    }

    //Add getters and setters here
    public List<Ingot> getIngots() {
        return ingots;
    }

    public void setIngots(List<Ingot> ingots) {
        this.ingots = ingots;
    }

    public Knapsack getKnapsack() {
        return knapsack;
    }

    public void setKnapsack(Knapsack knapsack) {
        this.knapsack = knapsack;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}