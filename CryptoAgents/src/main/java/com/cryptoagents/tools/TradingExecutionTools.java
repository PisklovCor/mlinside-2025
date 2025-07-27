package com.cryptoagents.tools;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TradingExecutionTools {
    
    private final Map<String, Map<String, Object>> orders = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> positions = new ConcurrentHashMap<>();
    
    @Description("Place a new trade order with specified parameters")
    public Map<String, Object> placeOrder(
            String symbol, 
            String orderType, // MARKET, LIMIT, STOP
            String side, // BUY, SELL
            BigDecimal quantity,
            BigDecimal price) {
        
        log.info("Placing {} {} order for {} shares of {} at {}", 
                orderType, side, quantity, symbol, price);
        
        String orderId = UUID.randomUUID().toString();
        Map<String, Object> order = new HashMap<>();
        order.put("orderId", orderId);
        order.put("symbol", symbol.toUpperCase());
        order.put("orderType", orderType.toUpperCase());
        order.put("side", side.toUpperCase());
        order.put("quantity", quantity);
        order.put("price", price);
        order.put("status", "PENDING");
        order.put("timestamp", LocalDateTime.now().toString());
        
        orders.put(orderId, order);
        
        // Simulate order execution for market orders
        if ("MARKET".equalsIgnoreCase(orderType)) {
            order.put("status", "FILLED");
            order.put("filledPrice", price);
            order.put("filledQuantity", quantity);
            order.put("filledTime", LocalDateTime.now().toString());
            
            // Update positions
            updatePosition(symbol, side, quantity, price);
        }
        
        return order;
    }
    
    @Description("Cancel an existing order by order ID")
    public Map<String, Object> cancelOrder(String orderId) {
        log.info("Cancelling order: {}", orderId);
        
        Map<String, Object> order = orders.get(orderId);
        if (order == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Order not found");
            error.put("orderId", orderId);
            return error;
        }
        
        if ("FILLED".equals(order.get("status"))) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Cannot cancel filled order");
            error.put("orderId", orderId);
            return error;
        }
        
        order.put("status", "CANCELLED");
        order.put("cancelledTime", LocalDateTime.now().toString());
        
        return order;
    }
    
    @Description("Get current status of an order")
    public Map<String, Object> getOrderStatus(String orderId) {
        log.info("Getting order status for: {}", orderId);
        
        Map<String, Object> order = orders.get(orderId);
        if (order == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Order not found");
            error.put("orderId", orderId);
            return error;
        }
        
        return new HashMap<>(order);
    }
    
    @Description("Get all open positions in the portfolio")
    public Map<String, Object> getOpenPositions() {
        log.info("Getting all open positions");
        
        Map<String, Object> result = new HashMap<>();
        result.put("positions", new HashMap<>(positions));
        result.put("totalPositions", positions.size());
        
        // Calculate total value
        BigDecimal totalValue = positions.values().stream()
                .map(p -> (BigDecimal) p.get("currentValue"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("totalValue", totalValue);
        
        return result;
    }
    
    @Description("Set stop loss and take profit levels for a position")
    public Map<String, Object> setStopLossAndTakeProfit(
            String symbol, 
            BigDecimal stopLoss, 
            BigDecimal takeProfit) {
        
        log.info("Setting SL: {} and TP: {} for {}", stopLoss, takeProfit, symbol);
        
        Map<String, Object> position = positions.get(symbol.toUpperCase());
        if (position == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Position not found");
            error.put("symbol", symbol);
            return error;
        }
        
        position.put("stopLoss", stopLoss);
        position.put("takeProfit", takeProfit);
        position.put("lastModified", LocalDateTime.now().toString());
        
        // Create stop loss and take profit orders
        Map<String, Object> slOrder = placeOrder(symbol, "STOP", "SELL", 
                (BigDecimal) position.get("quantity"), stopLoss);
        Map<String, Object> tpOrder = placeOrder(symbol, "LIMIT", "SELL", 
                (BigDecimal) position.get("quantity"), takeProfit);
        
        Map<String, Object> result = new HashMap<>();
        result.put("position", position);
        result.put("stopLossOrder", slOrder);
        result.put("takeProfitOrder", tpOrder);
        
        return result;
    }
    
    private void updatePosition(String symbol, String side, BigDecimal quantity, BigDecimal price) {
        String key = symbol.toUpperCase();
        
        if ("BUY".equalsIgnoreCase(side)) {
            positions.compute(key, (k, existing) -> {
                if (existing == null) {
                    Map<String, Object> newPosition = new HashMap<>();
                    newPosition.put("symbol", key);
                    newPosition.put("quantity", quantity);
                    newPosition.put("averagePrice", price);
                    newPosition.put("currentValue", quantity.multiply(price));
                    newPosition.put("openTime", LocalDateTime.now().toString());
                    return newPosition;
                } else {
                    BigDecimal existingQty = (BigDecimal) existing.get("quantity");
                    BigDecimal existingAvgPrice = (BigDecimal) existing.get("averagePrice");
                    
                    BigDecimal totalQty = existingQty.add(quantity);
                    BigDecimal totalCost = existingQty.multiply(existingAvgPrice).add(quantity.multiply(price));
                    BigDecimal newAvgPrice = totalCost.divide(totalQty, 2, RoundingMode.HALF_UP);
                    
                    existing.put("quantity", totalQty);
                    existing.put("averagePrice", newAvgPrice);
                    existing.put("currentValue", totalQty.multiply(price));
                    existing.put("lastModified", LocalDateTime.now().toString());
                    return existing;
                }
            });
        } else if ("SELL".equalsIgnoreCase(side)) {
            positions.computeIfPresent(key, (k, existing) -> {
                BigDecimal existingQty = (BigDecimal) existing.get("quantity");
                BigDecimal remainingQty = existingQty.subtract(quantity);
                
                if (remainingQty.compareTo(BigDecimal.ZERO) <= 0) {
                    return null; // Remove position
                } else {
                    existing.put("quantity", remainingQty);
                    existing.put("currentValue", remainingQty.multiply(price));
                    existing.put("lastModified", LocalDateTime.now().toString());
                    return existing;
                }
            });
        }
    }
}