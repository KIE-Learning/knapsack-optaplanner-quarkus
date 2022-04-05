# Getting started with OptaPlanner and Quarkus

This is a step-by-step guided exercise for a first experience with OptaPlanner running with Quarkus. It brings explanation of concepts along with the actual implementation and deployment of a new project.

Here's an overview of the sections:
- Introduction 
- Guided exercise
  - Step one: creating the project
  - Step 2: Implementing the domain model
  - Step 3: Implementing the planning solution
  - Step 4: Constraints
  - Step 5: Implement a rest application
  - Step 6: Deploying on OpenShift

## 1. Introduction 
[OptaPlanner](https://optaplanner.org) is an A.I. constraint satisfaction solver that provides a highly scalable platform to find optimal solutions to NP-complete and NP-hard problems. 

[OptaPlanner](https://optaplanner.org) enables us to write these solutions in plain Java, which makes this technology available to a large group of software developers. Furthermore, the OptaPlanner Quarkus extension lets us write our OptaPlanner application as a cloud-native micro-service.

<img src="https://raw.githubusercontent.com/openshift-instruqt/instruqt/master/assets/middleware/middleware-kogito/optaPlannerLogo.png" width="200"> 

In this lab you will start from scratch and learn how to implement an [OptaPlanner](https://www.optaplanner.org) application using [Quarkus](https://www.quarkus.io). Here you can learn core concepts of OptaPlanner and how to use it in your Java application.

### What you will build
In this scenario, we will build an OptaPlanner application on Quarkus that will solve the knapsack problem. The knapsack problem is an _NP-complete_ problem, which means it's not solvable in polynomial time. In other words, when the size of the problem grows, the time needed to solve the problem grows exponentially. For even relatively small problems, this means that finding the best solution can take billions of years.

### Knapsack Problem

You'll now solve an interesting problem: the *Knapsack Problem*.

The challenge of this scenario is that:
* Given: a knapsack that can contain a maximum weight and a set of items with a certain weight and value,
* Determine: the combination of items to include in the knapsack that maximizes the value of the contents without exceeding the knapsack weight limit.

 The knapsack problem is a problem in combinatoral optimization: given a knapsack that can contain a maximum weight and a set of items with a certain weight and value, determine the combination of items to include in the knapsack that maximizes the value of the contents without exceeding the knapsack weight limit.
  <center><img src="https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/Knapsack.svg/500px-Knapsack.svg.png" width="250" alt="Knapsack Problem"></center>

_image source: https://commons.wikimedia.org/wiki/File:Knapsack.svg, license: https://creativecommons.org/licenses/by-sa/2.5/deed.en_

## 2. Guided exercise: solving Knapsack with OptaPlanner and Quarkus

This repository contains a working solution for the knapsack problem in OptaPlanner. We'll quickly explain the different components:

- `Ingot`: This class is our `@PlanningEntity`, it represents the class that OptaPlanner needs to plan. Our `Ingot` has a `weight` and a `value`. Finally, it has a `Boolean` attribute which defines whether this `Ingot` instance has been selected in the knapsack. This `Boolean` is our `@PlanningVariable`.
- `Knapsack`: Defines the maximum weight of the knapsack in our problem.
- `KnapsackSolution`: Our @PlanningSolution. It's used to represent both the problem (i.e. the _uninitialized solution_,), the _working solution_, and the _best solution_, which is returned by OptaPlanner when solving is ended.
- `KnapsackConstraintProvider`: Provides the constrains (hard and soft constraints) of our solver, implemented in the OptaPlanner Constraint Streams API.
- `KnapsackController`: The Quarkus REST resource exposing our RESTful endpoint.
- `solverConfig.xml`: Configuration file for the OptaPlanner `Solver`. In this example, we configure a number of `MoveSelectors` for our _local search_ phase. The latter configuration is simply _power-tweaking_ of our solution by add a `SubPillarSwapMoveSelector`.
- `application.properties`: Quarkus configuration file in which, in this case, we set the maximum time the OptaPlanner `Solver` will run to 10 minutes.

  The first step is to create an application skeleton with OptaPlanner and Quarkus.

## 2.1. Step one, creating the project

The first step is to create an application skeleton with OptaPlanner and Quarkus.

We'll start with a basic Maven-based Quarkus application which has been generated using the Quarkus Maven Plugin.

### Hands on

#### Creating a basic project:

The easiest way to create a new Quarkus project is to use the Quarkus maven plugin. On your Terminal, execute the following commands:
```
mkdir ~/optaplanner/
cd ~/optaplanner
mvn io.quarkus:quarkus-maven-plugin:2.5.0.Final:create -DprojectGroupId=com.redhat -DprojectArtifactId=knapsack-optaplanner-quarkus -DclassName="com.redhat.knapsackoptaplanner.solver.KnapsackResource" -Dpath="/knapsack" -Dextensions="org.optaplanner:optaplanner-quarkus,org.optaplanner:optaplanner-quarkus-jackson,quarkus-resteasy-jackson,quarkus-smallrye-openapi"
```

 The Quarkus Maven plugin is now generating a basic Quarkus application that includes the OptaPlanner extension, and it is located inside the `knapsack-optaplanner-quarkus` subdirectory.

 For the purpose of this exercise, we won't use the automatically generated unit tests.

 On your terminal, copy and paste the following command to remove the automatically generated unit-test classes:
```
rm -rf ~/optaplanner/knapsack-optaplanner-quarkus/src/test/java/com
```

  Still on your terminal, let's go to the `knapsack-optaplanner-quarkus` directory:
```
cd ~/optaplanner/knapsack-optaplanner-quarkus
```

  We can now run our new application for the first time!

#### Running the Application

  The next command starts the OptaPlanner application in Quarkus development mode.

*TIP: Quarkus dev mode enable developers to keep the application running while we implement application logic. OptaPlanner and Quarkus will hot reload the application (update changes while the application is running) when it is accessed and changes have been detected.*

  On your terminal, start your app:

```shell
mvn clean compile quarkus:dev
```

You can now see Maven downloading all the required libraries. Once it's done, the application should start in dev mode.

*TIP: Notice it returns a WARN message saying it can't find any classes annotated with `@PlanningSolution`. This is expected! We will implement these classes later.*

Next, hit `CTRL-C` or type `q` to stop Quarkus.

### Adding project package structure
Next, you can use your IDE or your terminal to add a couple of folders to our application. These folders will be the packages that will hold the classes we'll create on the next steps.

If using the terminal, execute:
```shell
mkdir -p ~/optaplanner/knapsack-optaplanner-quarkus/src/main/java/com/redhat/knapsackoptaplanner/domain
mkdir -p ~/optaplanner/knapsack-optaplanner-quarkus/src/main/java/com/redhat/knapsackoptaplanner/solver
```

### Congratulations!

  You've practiced the initial steps of an OptaPlanner project by creating the skeleton of a basic Quarkus application, and starting it using Quarkus development mode.

  In the next step we'll start coding and add the domain model of our application.

##  2.2. Step 2, Implementing the domain model

### Foundation concepts you must check before moving foward
In this challenge, you will create the Java classes and configure them based on key OptaPlanner concepts. Every OptaPlanner application has planning entities (`@PlanningEntity` annotation) and planning variables (`@PlanningVariable` annotation), so let's familiar with these concepts:

**Planning Entities**
Planning entities are the entities in our domain that OptaPlanner _needs to plan_. In the knapsack problem, these are the ingots because these are the entities that are either put into the knapsack or not.

**Planning Variables**

Planning variables are properties of a planning entity that specify a planning value _that changes during planning_. In the knapsack problem, this is the property that tells OptaPlanner whether or not the ingot is _selected_. That is, whether or not it is put in the knapsack.
Note that in this example we have a single knapsack. If we have multiple knapsacks, the actual knapsack is the planning variable, because an ingot can be placed in different knapsacks.

###  Hands-on

In the previous step we've created a skeleton OptaPlanner application with Quarkus and started the application in Quarkus development mode. In this step we'll create the domain model of our application.

#### Implement the Ingot class

In your IDE of choice:
1. Navigate to the folder `src/main/java/com/redhat/knapsackoptaplanner/domain`
2. Add a new file `Ingot.java` 
3. Open the `Ingot.java` class.

Now, let's add the following initial code your class. Copy and paste the source code into the class `Ingot.java`:

```java
package com.redhat.knapsackoptaplanner.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * Ingot
    */

  //Add PlanningEntity annotation

  public class Ingot {

    private int weight;

    private int value;

    //Add Planning Variable annotation

    private Boolean selected;

    public Ingot() {
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

}
```

#### Configuring our Planning Entity

##### Planning Entity

First, let's tell OptaPlanner that this Ingot class is our `PlanningEntity` class. To do this, on line 11, annotate the class by adding:
```java
  @PlanningEntity
```

It should look like this:

```java
//Add PlanningEntity annotation
@PlanningEntity
public class Ingot {
```

##### Planning Variable

Next, we need to configure our planning variable.

In this example, the planning variable (the property that changes during planning) is the `selected` attribute of the planning entity class.
Annotate this attribute with the `@PlanningVariable` annotation and specify the _valuerange provider_. This is the entity in our application that provides the range of possible values of our planning variable.

On line 19, annotate the `selected` attribute by adding:

```java
    @PlanningVariable(valueRangeProviderRefs = "selected")
```

It should look like this:
```java
//Add Planning Variable annotation
@PlanningVariable(valueRangeProviderRefs = "selected")
private Boolean selected;
```

In this example, because our planning variable is a `Boolean` value, the _valuerange_ is simply `true` and `false`. We will define this provider in the next step.

#### Knapsack class

We now need an object that defines the maximum weight of our knapsack. So let's implement a simple `Knapsack` class that has a `maxWeight` attribute that can hold this value.

In your IDE of choice:
1. Navigate to the folder `src/main/java/com/redhat/knapsackoptaplanner/domain`
2. Add a new file `Knapsack.java`
3. Open the `Knapsack.java` class.

Now, copy and paste the following `Knapsack.java`. It's a class with a single `maxWeight` attribute:
```java
package com.redhat.knapsackoptaplanner.domain;

public class Knapsack {

    private int maxWeight;

    public Knapsack() {
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

}
```
#### Save your work

Before proceeding, remember to save both files by clicking on the save icon next to each filename.

#### Congratulations!

You successfully implemented the domain model of our OptaPlanner application, which is a key piece of our planning solution.

In the next step, we will implement the actual _Planning Solution_ of this app.

##  2.3. Step 3, Implementing the planning solution

In the previous step we've implemented the domain model of the application. Now it's time to implement the *planning solution*.

The planning solution of represents both:
- the problem (i.e. the uninitialized solution),
- the working solution, and
- the best solution which is the solution returned by OptaPlanner when solving is ended.

So, since we need to represent the problem, working solution and best solution, in this knacksack problem, an inplementation should contains:

* The collection of planning entities that need to be planned. In this case, this is a list of `Ingots`.
* Zero or more collections/ranges of planning variables. In this simple example we only have a range of boolean values (i.e. `true` and `false`) that indicate whether an ingot has been selected or not.
* Possible problem fact properties. These are properties that are neither a planning entity nor a planning variable, but are required by the constraints during solving. In this example the `Knapsack` is such a property because the problem requires the maximum weight of the knapsack in the constraint evaluation.
* The `Score` of the solution. This contains the score calculated by the OptaPlanner `ScoreCalculator` based on the hard and soft constraints.

### Hands-on

#### PlanningSolution

As stated before, we'll now implement a `PlanningSolution` class with:

* list of `Ingots`: the collection of planning entities (_need to be planned_).
* Range of boolean values (`true` and `false`) to indicate whether an ingot has been selected or not: zero or more collections/ranges of planning variables.
* The `Knapsack` which holds the maximum weight of the knapsack in the constraint evaluation: possible problem fact properties
* The `ScoreCalculator`: representing the `Score` of the solution calculated by OptaPlanner based on the hard and soft constraints.

It's time to implement the skeleton of our `KnapsackSolution` class.

In your IDE of choice:
1. Navigate to the folder `src/main/java/com/redhat/knapsackoptaplanner/domain`
2. Add a new file `KnapsackSolution.java`
3. Open the `KnapsackSolution.java` class.

Copy and paste the source code below into the new `KnapsackSolution.java` class:

```java
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

     public class KnapsackSolution {

       //Add Ingots here


       //Add Knapsack here

       //Add selected valuerangeprovider here

       //Add PlanningScore here

       public KnapsackSolution() {
       }

       //Add getters and setters here
     }
```

To mark this class as a `PlanningSolution` class, add the `@PlanningSolution` annotation on line 15:

 ```java
   @PlanningSolution
 ```
 It should look like:
 ```java
 //Add PlanningSolution annotation here
 @PlanningSolution
 public class KnapsackSolution {
 ```

#### Planning Entities

 We can now add the collection of planning entities to our class. As stated earlier, in this implementation the list of ingots is a collection of planning entities.

 Let's now add an ingots list and tell OptaPlanner that this is the collection of planning entities.

 On line 19 add the following code on your class, and notice the annotation `@PlanningEntityCollectionProperty` annotation.
 ```java
     @PlanningEntityCollectionProperty
     private List<Ingot> ingots;
 ```

#### Value range Provider

 Next, we can add the _value range provider_ to our solution class. This is the provider of the value range of our `selected` planning variable in the `Ingot` planning entity class.

 Because this planning variable is a boolean value, we need to create a `Boolean` value range. You can add the following code to your class:

 ```java
     @ValueRangeProvider(id = "selected")
     public CountableValueRange<Boolean> getSelected() {
       return ValueRangeFactory.createBooleanValueRange();
     }
 ```

#### Problem Facts

The constraints that we will implement in the following step of our scenario need to know the maximum weight of our knapsack to be able to determine whether the knapsack can carry the total weight of the selected ingots.

For this reason, our constraints need to have access to the `Knapsack` instance.

Let's now create on line 23, a knapsack attribute in the planning solution and annotate it with the `@ProblemFactProperty` annotation.

 _TIP: (note that there is also an `@ProblemFactCollectionProperty` annotation for collections)_

 ```java
     @ProblemFactProperty
     private Knapsack knapsack;
 ```

#### Creating the Planning Score

 The next thing we need to add is the `PlanningScore`.

 In this knapsack problem we have two score types: a _hard score_ and a _soft score_.

 **INFO:** In OptaPlanner, a broken hard score defines an _infeasible solution_. The soft score is the score that we want to optimize.

 In our knapsack application, an example of:
- a broken hard score:  if the total weight of the selected ingots is higher than the maximum weight of the knapsack.
- a soft score constraint: the total value of the selected ingots. We want a solution with the highest possible values.

  Next, add to the planning solution class a `HardSoftScore` attribute annotated with `@PlanningScore`:

 ```java
      @PlanningScore
      private HardSoftScore score;
  ```

#### Getters and Setters

  Finally, we also need to create the _getters and setters_ for our attributes.

  ```java
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
```

That's it. We're now ready to define our constraints.

### Congratulations!

In this step you've implemented the Planning Solution of your application. Well done! In the next step we will implement the constraints of our problem using `ConstraintStreams`.

## 2.4. Step 4, Constraints

What are Constraints?

Constraints define how the score of a solution is calculated. Based on the current assignment of planning variables to planning entities, we can calculate a score for the solution using constraints. This example, as stated earlier, uses a _hard_ and _soft_ score.

A _hard_ score defines an infeasible solution and the _soft_ score is the score that we want to optimize. Our constraints will calculate these scores.

In this example we will implement two constraints. The first constraint states that a hard constraint is broken when the total weight of the selected ingots is greater than the maximum weight of the knapsack. That is, if we select ingots that have a total weight that is greater than the maximum weight of our knapsack, the solution is infeasible.

The soft constraint, the score that we want to optimize, is the total value of the ingots. That is, we want to find the solution that maximizes our total value. For this we will implement a constraint that calculates this as a soft score.

In this challenge we will implement our constraints using use the Constraint Streams API.
So, what about Constraint Streams?  

OptaPlanner provides various options to implement our constraints:
  - **Easy Java**: Java implementation that recalculates the full score for every move. Easy to write but extremely slow. Not recommended for production use.
  - **Incremental Java**: Java implementation that does incremental score calculation on every move. Fast, but very hard to write and maintain. Not recommended for production use.
  - **Drools**: Rule-based constraints written in DRL. Incremental and fast calculation of constraints. Requires some knowledge of Drools.
  - **Constraint  Streams**: Constraints written in an API inspired by Java Streams. Incremental and fast calculation of constraints. Requires knowledge of the Streams API.

In this challenge we will implement our constraints using use the Constraint Streams API.

### Hands-on 

In the previous step you've implemented the `PlanningSolution` of our application. Let's now implement the constraint rules.

We will start by implementing the `ConstraintProvider`. The implementation class is automatically picked up by the OptaPlanner runtime without the need for any configuration.

We will implement the `KnapsackConstraintProvider` class. In your IDE of choice:

1. Navigate to the folder `src/main/java/com/redhat/knapsackoptaplanner/solver`
2. Add a new file `KnapsackConstraintProvider.java` 
3. Open the `KnapsackConstraintProvider.java`.

Copy the class below and paste in the new class. It is a basic implementation of a ConstraintProvider interface:

```java
package com.redhat.knapsackoptaplanner.solver;

import com.redhat.knapsackoptaplanner.domain.Ingot;
import com.redhat.knapsackoptaplanner.domain.Knapsack;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

public class KnapsackConstraintProvider implements ConstraintProvider {

  @Override
  public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
      return new Constraint[] {
          maxWeight(constraintFactory),
          maxValue(constraintFactory)
      };
  }

  /*
  * Hard constraint
  */
  //Add hard constraint here


  /*
  * Soft constraint
  */
  //Add soft constraint here


}
```

**The hard constraint**: sums up the weight of all selected ingots and compares this with the maximum weight of the knapsack.

Add the hard constraint to the `KnapsackConstraintProvider` class on line 26:
```java
    private Constraint maxWeight(ConstraintFactory constraintFactory) {
      return constraintFactory.from(Ingot.class).filter(i -> i.getSelected())
              .groupBy(ConstraintCollectors.sum(i -> i.getWeight())).join(Knapsack.class)
              .filter((ws, k) -> ws > k.getMaxWeight())
              .penalize("Max Weight", HardSoftScore.ONE_HARD, (ws, k) -> ws - k.getMaxWeight());
    }
```

**The soft constraints**: sums up all the values of the selected ingots.
Add the soft constraint to the `KnapsackConstraintProvider` class:
```
    private Constraint maxValue(ConstraintFactory constraintFactory) {
      return constraintFactory.from(Ingot.class)
              .filter(Ingot::getSelected)
              .reward("Max Value", HardSoftScore.ONE_SOFT, Ingot::getValue);
    }
```

#### Congratulations!

You've implemented your first OptaPlanner constraints using the Constraint Streams API. In the next challenge you'll implement a RESTful resource and test the application!

## 2.5. Step 5, Implement a rest application

In the previous step we've implemented the constraints of the application using the `ConstraintStreams` API. It's now time to create the RESTful resource of our application and take the application for a test-drive.

### Hands-on

#### KnapsackResource: Creating and testing the RESTful API

When we created the initial OptaPlanner application using the Quarkus Maven
plugin, we defined the resource class of our RESTful endpoint (being `KnapsackResource`).

Now we should implement the skeleton of our `KnapsackSolution` class. To do this, switch to your IDE of choice and:
- navigate to `src/main/java/com/redhat/knapsackoptaplanner/solver`
- open the `KnapsackResource.java` file:

The `KnapsackResource` class is implemented as a Quarkus JAX-RS service. We'll replace the code with a complete class now.
Copy the following code and notice that it creates a Solver Manager.

**INFO:** The OptaPlanner `SolverManager` instance manages the `Solver` instances that will solve our problem.

```java
  package com.redhat.knapsackoptaplanner.solver;

  import java.util.UUID;
  import java.util.concurrent.ExecutionException;
  import javax.inject.Inject;
  import javax.ws.rs.Consumes;
  import javax.ws.rs.POST;
  import javax.ws.rs.Path;
  import javax.ws.rs.Produces;
  import javax.ws.rs.core.MediaType;
  import com.redhat.knapsackoptaplanner.domain.KnapsackSolution;
  import org.optaplanner.core.api.solver.SolverJob;
  import org.optaplanner.core.api.solver.SolverManager;

  @Path("/knapsack")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public class KnapsackResource {

    @Inject
    private SolverManager<KnapsackSolution, UUID> solverManager;

    @POST
    @Path("/solve")
    public KnapsackSolution solve(KnapsackSolution problem) {
      UUID problemId = UUID.randomUUID();

      // Submit the problem to start solving
      SolverJob<KnapsackSolution,UUID> solverJob = solverManager.solve(problemId, problem);
      KnapsackSolution solution;

      try {
        // Wait until the solving ends
        solution = solverJob.getFinalBestSolution();
      } catch (InterruptedException | ExecutionException e) {
        throw new IllegalStateException("Solving failed.", e);
      }

      return solution;
    }
  }
```

* A more detailed information on how this works:
  * The `SolverManager` should accept (uninitialized) PlanningSolutions in the problem;
  * It then passes this problem to a managed `Solver` that runs on a separate thread to solve it;
  * The `SolverJob runs until solving ends
  * After the `SolverJob` finishes, we can retrieve the final best solution.

#### Configuring the Solver

By default, OptaPlanner keeps on solving the problem indefinitely. In order to change this we should configure a _termination strategy_.

**INFO:** A _termnination strategy_ tells OptaPlanner when to stop solving, for example based on the number of seconds spent, or if a score has not improved in a specified amount of time.

In a Quarkus-based application, we can set this _termination strategy_ by simply adding a new property in the `application.properties` configuration file.

Let's do it. First, open the application.properties using your IDE of choice:
  - Locate the directory: `knapsack-optaplanner-quarkus/src/main/resources/`
  - Open the file `application.properties`

Next, add the _termination strategy_ configuration property as you see below:
```properties
  # Configuration file
  # key = value
  quarkus.optaplanner.solver.termination.spent-limit=10s
```

Save the files.

The `quarkus.optaplanner.solver.termination.spent-limit` property is set to 10 seconds, which means that the solver will stop solving after 10 seconds and return the best result found so far.

#### Running the Application

Let's now switch to the terminal start our application. We'll use Quarkus development mode, so we can later access the Swagger-UI of our application.

```shell
  cd ~/optaplanner/knapsack-optaplanner-quarkus/
  mvn quarkus:dev
```

**TIP:** Hitting this endpoint will force the OptaPlanner Quarkus application to do a hot-reload and recompile and deploy the changes we made in our application "on-the-fly".

Once the app is up and running we can now use a second terminal tab to send it a RESTful request with a knapsack problem.

**IMPORTANT:** Note that it will take 10 seconds for the response to return because we've set the OptaPlanner termination strategy to 10 seconds.

Switch to a second terminal  tab and run the solver via rest api using the command below:
```shell
  curl --location --request POST 'http://localhost:8080/knapsack/solve' --header 'Accept: application/json' --header 'Content-Type: application/json' --header 'Content-Type: text/plain' --header 'Cookie: JSESSIONID=0C6C24091A814A4A0431ED5E32CE6B45' --data-raw '{"knapsack": { "maxWeight": 10 }, "ingots" : [ { "weight": 4, "value": 15 }, { "weight": 4, "value": 15 }, { "weight": 3, "value": 12}, { "weight": 3, "value": 12 }, { "weight": 3, "value": 12 }, { "weight": 2, "value": 7 }, { "weight": 2, "value": 7 }, { "weight": 2, "value": 7 }, { "weight": 2, "value": 7 }, { "weight": 2, "value": 7} ]}'
```

The response should show which ingots have been selected. The selected ingots will have their `selected` attribute set to `true`. You should see something like:

```json
  {"ingots":[{"weight":4,"value":15,"selected":true},{"weight":4,"value":15,"selected":true},{"weight":3,"value":12,"selected":false},{"weight":3,"value":12,"selected":false},{"weight":3,"value":12,"selected":false},{"weight":2,"value":7,"selected":true},{"weight":2,"value":7,"selected":false},{"weight":2,"value":7,"selected":false},{"weight":2,"value":7,"selected":false},{"weight":2,"value":7,"selected":false}],"knapsack":{"maxWeight":10},"score":"0hard/37soft","selected":{"size":2,"empty":false}}
```

### Congratulations!
  You've implemented the RESTful endpoint of the application using the Quarkus! You also tested and solved a knapsack problem.
  Well done! In the next step we will deploy this application to OpenShift to run our OptaPlanner solution as a true cloud-native application.

## 2.6. Step 6, Deploying on OpenShift

In the previous step we've implemented the RESTful resource of our OptaPlanner Quarkus application and solved a knapsack problem. In this step of the scenario, we will deploy our service to OpenShift and scale it up to be able to handle production load.

To execute this part of the lab, you need an OpenShift environment. You can use a local OpenShift or a provisioned one. To learn how you can run OpenShift in your machine please refer the article: [How can I run OpenShift on my own computer for development?
](https://developers.redhat.com/openshift/local-openshift)

### Hands-on

Now that we have our app built, let's move it into containers and into the cloud.

#### Install OpenShift extension

Quarkus offers the ability to automatically generate OpenShift resources.
  **TIP:** The OpenShift extension is actually a wrapper extension that brings together the [kubernetes](https://quarkus.io/guides/deploying-to-kubernetes) and [container-image-s2i](https://quarkus.io/guides/container-image#s2i) extensions with defaults so that it’s easier for the user to get started with Quarkus on OpenShift.

Run the following command to add it to our project in your terminal:
```shell
  cd ~/optaplanner/knapsack-optaplanner-quarkus/
  mvn quarkus:add-extension -Dextensions="openshift" -f ~/optaplanner/knapsack-optaplanner-quarkus
```

#### Login to OpenShift

The first thing we will do, is log in our OpenShift environment using the CLI. Using `terminal 1`, log in using the oc client:

```
oc login -u developer -p developer
```

You should see

```bash
Login successful.
You don't have any projects. You can try to create a new project, by running `oc new-project <projectname>`
```

**TIP:** The OpenShift CLI is accessed using the command _oc_. With it you can administrate the entire OpenShift cluster and deploy new applications. Users familiar with Kubernetes will be able to adapt to OpenShift quickly. _oc_ provides all of the functionality of _kubectl_, along with additional functionality to make it easier to work with OpenShift.

#### Create project
Now, let's create an OpenShift project to deploy our service. In the terminal run the following command:
```
oc new-project knapsack-optaplanner --display-name="Knapsack OptaPlanner Solver"
```

OpenShift ships with a web-based console that allows users to perform various tasks via a browser.
  To get a feel for how the web console works, click the `OpenShift Web Console` tab.

### Logging in with the Web Console

To begin, let's log into our cluster to start working. This can be done by clicking the *OpenShift Web Console* tab near the top of your screen.
  You will then be able to login with admin permissions with:

* **Username:** ``developer``
* **Password:** ``developer``

![Web Console Login](https://raw.githubusercontent.com/openshift-instruqt/instruqt/master/assets/middleware/pipelines/web-console-login.png)

After we authenticate to the web console, we see a list of projects that we have permission to work with.

![Projects list OCP](https://raw.githubusercontent.com/openshift-instruqt/instruqt/master/assets/middleware/middleware-kogito/optaplanner-knapsack-project-ocp.png)

Click on knapsack-optaplanner to go to the project overview page which lists all of the routes, services, deployments, and pods that are running as part of this project

There's nothing there now, but that's about to change.

#### Deploy application to OpenShift

Now let's deploy the application itself.

Run the following command which will build and deploy a Quarkus application using the OpenShift extension (this will take a few minutes to complete as it rebuilds application, generates a container image and pushes it into OpenShift):

```shell
  mvn clean package -f ~/optaplanner/knapsack-optaplanner-quarkus \
  -Dquarkus.kubernetes-client.trust-certs=true \
  -Dquarkus.container-image.build=true \
  -Dquarkus.kubernetes.deploy=true \
  -Dquarkus.kubernetes.deployment-target=openshift \
  -Dquarkus.openshift.expose=true \
  -Dquarkus.openshift.labels.app.openshift.io/runtime=quarkus
```

The output should end with `BUILD SUCCESS`.

Finally, make sure it's actually done rolling out:
```
  oc rollout status -w dc/knapsack-optaplanner-quarkus
```

Wait for that command to report following before continuing.

This should only take a few seconds to complete the scaling. The application is now ready to take production load.

![Projects deployed](https://raw.githubusercontent.com/openshift-instruqt/instruqt/master/assets/middleware/middleware-kogito/optaplanner-knapsack-project-deployed-ocp.png)

Finally, test our application solver using the command line to send a request, just like we did on the previous steps. This time, we're not triggering localhost, instead, we'll invoke the application that's now deployed in a container in OpenShift.

Let's grab the application URL. From your terminal run this command to get the application's Route:
```bash
  APP_ROUTE=`oc get route knapsack-optaplanner-quarkus -n knapsack-optaplanner -o jsonpath='{"http://"}{.spec.host}{"/knapsack"}{"\n"}'`
```
Next, let's do our request. We'll send the information about the knapsack and a list of ingots. Let's use OptaPlanner to solve this for us:

```shell
  curl --location --request POST "${APP_ROUTE}/solve" --header 'Accept: application/json' --header 'Content-Type: application/json' --header 'Content-Type: text/plain' --header 'Cookie: JSESSIONID=0C6C24091A814A4A0431ED5E32CE6B45' --data-raw '{"knapsack": { "maxWeight": 10 }, "ingots" : [ { "weight": 4, "value": 15 }, { "weight": 4, "value": 15 }, { "weight": 3, "value": 12}, { "weight": 3, "value": 12 }, { "weight": 3, "value": 12 }, { "weight": 2, "value": 7 }, { "weight": 2, "value": 7 }, { "weight": 2, "value": 7 }, { "weight": 2, "value": 7 }, { "weight": 2, "value": 7} ]}'
  ```

Check the output with the ingots tagged with `"selected"=true`.

You've just created a cloud-native optimization application, deployed it in the cloud and tested it! Now, let's do one final task: scaling!

#### Scaling the application
In order to be able to handle production load and have high availability semantics, we need to scale the application and add a number of extra running pods.

Run this command to scale the number of PODs via the OpenShift oc client:

```
 oc scale --replicas=10 dc/knapsack-optaplanner-quarkus
```

Finally, open the `OpenShift Web Console`, and click on the circle that represents the application. You should be able to see the app scaling dynamically up to 10 pods.

![Projects list OCP](https://raw.githubusercontent.com/openshift-instruqt/instruqt/master/assets/middleware/middleware-kogito/optaplanner-knapsack-project-scaled-up-ocp.png)

This should only take a few seconds to complete the scaling. Your application is now ready to take production load!!

### Congratulations!

In this scenario you've had a glimpse of the power of OptaPlanner apps on a Quarkus runtime on OpenShift. We've packaged our Knapsack OptaPlanner solver in a container image, deployed it on OpenShift, and solved a knapsack problem. Finally, we've scaled the environment to 10 pods to be able to serve production load. Well done!