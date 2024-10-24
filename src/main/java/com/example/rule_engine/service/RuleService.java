package com.example.rule_engine.service;
import com.example.rule_engine.Node;
import org.springframework.stereotype.Service;

@Service
public class RuleService {

    public Node createRule(String ruleString) {
        // Logic to parse ruleString and generate AST
        // For now, we can assume this function returns a simple test AST.

        // Example: if ruleString is "age > 30", create an operand node
        Node conditionNode = new Node("operand", "age > 30");

        // Example: if ruleString is "age > 30 AND salary > 50000", create an AST
        Node leftCondition = new Node("operand", "age > 30");
        Node rightCondition = new Node("operand", "salary > 50000");
        Node root = new Node("operator", leftCondition, rightCondition, "AND");

        return root; // Return the root of the AST
    }

    // More methods for combining rules and evaluating them will go here
}

