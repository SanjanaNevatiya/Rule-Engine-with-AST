package com.example.rule_engine.controller;

import com.example.rule_engine.Node;
import com.example.rule_engine.RuleEntity;
import com.example.rule_engine.repository.RuleRepository;
import com.example.rule_engine.service.RuleParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.rule_engine.service.RuleEvaluator;
import com.example.rule_engine.service.RuleCombiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rules")
public class RuleController {

    @Autowired
    private RuleParser ruleParser;

    @Autowired
    private RuleEvaluator ruleEvaluator;

    @Autowired
    private RuleCombiner ruleCombiner;

    @Autowired
    private RuleRepository ruleRepository;

    // DTO for creating a rule
    public static class CreateRuleRequest {
        public String ruleString; // This should match the JSON field name
    }

    @PostMapping("/create")
    public ResponseEntity<String> createRule(@RequestBody CreateRuleRequest createRuleRequest) {
        // Ensure ruleString is not null or empty
        if (createRuleRequest == null || createRuleRequest.ruleString == null || createRuleRequest.ruleString.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Rule string cannot be null or empty");
        }

        String ruleString = createRuleRequest.ruleString; // Get the ruleString from the request body

        Node ast;
        try {
            ast = ruleParser.parseRule(ruleString); // Try parsing the rule string
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid rule: " + e.getMessage());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String astJson;
        try {
            astJson = objectMapper.writeValueAsString(ast); // Convert AST to JSON
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing AST to JSON");
        }

        RuleEntity ruleEntity = new RuleEntity();
        ruleEntity.setRuleString(ruleString);
        ruleEntity.setAst(astJson);
        ruleRepository.save(ruleEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body("Rule created with ID: " + ruleEntity.getId());
    }


    @PostMapping("/evaluate")
    public ResponseEntity<Boolean> evaluateRule(@RequestBody Map<String, Object> request) {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Deserialize userData from request
        Map<String, Object> userData = (Map<String, Object>) request.get("userData");

        // Deserialize ast to Node
        Node condition;
        try {
            String astJson = objectMapper.writeValueAsString(request.get("ast"));
            condition = objectMapper.readValue(astJson, Node.class); // Convert ast JSON to Node
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false); // Error during deserialization
        }

        // Check if the condition is null
        if (condition == null) {
            return ResponseEntity.badRequest().body(false); // or return an appropriate error response
        }

        boolean result = ruleEvaluator.evaluateRule(condition, userData);
        return ResponseEntity.ok(result);
    }

    // Add your method to parse the condition string into a Node here
    private Node parseConditionToAST(String condition) {
        String[] tokens = condition.split(" ");
        Node root = new Node();
        
        if (tokens.length == 3) {
            root.setType("operand");
            root.setValue(condition);
        } else if (tokens.length > 3) {
            root.setType("operator");
            root.setValue(tokens[1]);

            Node left = new Node();
            left.setType("operand");
            left.setValue(tokens[0]);

            Node right = new Node();
            right.setType("operand");
            right.setValue(tokens[2]);

            root.setLeft(left);
            root.setRight(right);
        }
        return root;
    }

    @PostMapping("/combine")
    public ResponseEntity<String> combineRules(@RequestParam Long rule1Id, @RequestParam Long rule2Id, @RequestParam String operator) {
        RuleEntity rule1 = ruleRepository.findById(rule1Id).orElse(null);
        RuleEntity rule2 = ruleRepository.findById(rule2Id).orElse(null);

        if (rule1 == null || rule2 == null) 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rules not found");

        Node ast1 = ruleParser.parseRule(rule1.getRuleString());
        Node ast2 = ruleParser.parseRule(rule2.getRuleString());

        Node combinedAst = ruleCombiner.combineRules(ast1, ast2, operator);

        ObjectMapper objectMapper = new ObjectMapper();
        String combinedAstJson;
        try {
            combinedAstJson = objectMapper.writeValueAsString(combinedAst);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error combining rules");
        }

        RuleEntity combinedRule = new RuleEntity();
        combinedRule.setRuleString("Combined Rule");
        combinedRule.setAst(combinedAstJson);
        ruleRepository.save(combinedRule);

        return ResponseEntity.status(HttpStatus.CREATED).body("Combined rule created with ID: " + combinedRule.getId());
    }
}
