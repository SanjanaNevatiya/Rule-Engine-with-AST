package com.example.rule_engine.service;


import org.springframework.stereotype.Service;

import com.example.rule_engine.Node;
import java.util.Stack;
@Service
public class RuleParser {

	public Node parseRule(String rule) {
	    if (rule == null || rule.trim().isEmpty()) {
	        throw new IllegalArgumentException("Rule cannot be null or empty");
	    }

	    Stack<Node> stack = new Stack<>();
	    String[] tokens = rule.split(" ");
	    Node currentNode = null;

	    for (String token : tokens) {
	        switch (token) {
	            case "AND":
	            case "OR":
	                Node operatorNode = new Node("operator", token);
	                operatorNode.setLeft(currentNode);
	                stack.push(operatorNode);
	                break;
	            case "(":
	                stack.push(null); // Start a new expression group
	                break;
	            case ")":
	                while (!stack.isEmpty() && stack.peek() != null) {
	                    currentNode = stack.pop();
	                }
	                if (!stack.isEmpty()) {
	                    stack.pop(); // Pop the null marker
	                }
	                break;
	            default:
	                Node operandNode = new Node("operand", token);
	                currentNode = operandNode;

	                if (!stack.isEmpty() && stack.peek() != null) {
	                    Node operator = stack.pop();
	                    operator.setRight(currentNode);
	                    currentNode = operator;
	                }
	                break;
	        }
	    }

	    return currentNode; // Final root of the AST
	}

}

