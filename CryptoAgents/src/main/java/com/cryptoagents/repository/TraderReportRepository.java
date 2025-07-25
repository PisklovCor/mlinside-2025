package com.cryptoagents.repository;

import com.cryptoagents.model.TraderReport;
import com.cryptoagents.model.enums.ActionRecommendation;
import com.cryptoagents.model.enums.OrderType;
import com.cryptoagents.model.enums.TimeInForce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с отчетами трейдера.
 * 
 * Предоставляет методы для сохранения и поиска отчетов трейдера,
 * включая фильтрацию по различным критериям.
 */
@Repository
public interface TraderReportRepository extends JpaRepository<TraderReport, Long> {
    
    /**
     * Находит отчеты трейдера по торговому действию.
     * 
     * @param actionRecommendation торговое действие для фильтрации
     * @return список отчетов трейдера с указанным действием
     */
    List<TraderReport> findByActionRecommendation(ActionRecommendation actionRecommendation);
    
    /**
     * Находит отчеты трейдера по тикеру и торговому действию.
     * 
     * @param ticker символ тикера криптовалюты
     * @param actionRecommendation торговое действие для фильтрации
     * @return список отчетов трейдера для тикера с указанным действием
     */
    List<TraderReport> findByTickerAndActionRecommendation(String ticker, ActionRecommendation actionRecommendation);
    
    /**
     * Находит отчеты трейдера по типу ордера.
     * 
     * @param orderType тип ордера для фильтрации
     * @return список отчетов трейдера с указанным типом ордера
     */
    List<TraderReport> findByOrderType(OrderType orderType);
    
    /**
     * Находит отчеты трейдера по времени действия.
     * 
     * @param timeInForce параметр времени действия для фильтрации
     * @return список отчетов трейдера с указанным временем действия
     */
    List<TraderReport> findByTimeInForce(TimeInForce timeInForce);
    
    /**
     * Находит самый последний отчет трейдера для указанного тикера.
     * 
     * @param ticker символ тикера криптовалюты
     * @return optional, содержащий самый последний отчет трейдера
     */
    Optional<TraderReport> findTopByTickerOrderByCreatedAtDesc(String ticker);
    
    /**
     * Находит все отчеты трейдера с рекомендацией покупки.
     * 
     * @return список отчетов трейдера с действием BUY
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.actionRecommendation = 'BUY'")
    List<TraderReport> findAllBuyRecommendations();
    
    /**
     * Находит все отчеты трейдера с рекомендацией продажи.
     * 
     * @return список отчетов трейдера с действием SELL
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.actionRecommendation = 'SELL'")
    List<TraderReport> findAllSellRecommendations();
    
    /**
     * Находит все отчеты трейдера с рекомендацией удержания.
     * 
     * @return список отчетов трейдера с действием HOLD
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.actionRecommendation = 'HOLD'")
    List<TraderReport> findAllHoldRecommendations();
    
    /**
     * Находит отчеты трейдера с соотношением риск/прибыль выше указанного порога.
     * 
     * @param minRatio minimum acceptable risk-reward ratio
     * @return list of trader reports with ratio >= threshold
     */
    @Query("SELECT tr FROM TraderReport tr WHERE tr.riskRewardRatio >= :minRatio")
    List<TraderReport> findByRiskRewardRatioGreaterThanEqual(@Param("minRatio") double minRatio);
} 