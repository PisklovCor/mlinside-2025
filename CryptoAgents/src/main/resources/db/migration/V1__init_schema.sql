-- Миграция схемы базы данных CryptoAgents V1
-- Эта миграция создает начальную схему базы данных для системы мультиагентного анализа криптовалют
-- Совместима с PostgreSQL и H2 (режим PostgreSQL)

-- Создание таблицы analysis_reports для комплексных отчетов анализа
CREATE TABLE IF NOT EXISTS analysis_reports (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    symbol VARCHAR(10) NOT NULL,
    analysis_result TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Создание базовой таблицы analysis_results с использованием стратегии JOINED наследования
CREATE TABLE IF NOT EXISTS analysis_results (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    agent_type VARCHAR(31) NOT NULL, -- Дискриминаторная колонка для JOINED наследования
    ticker VARCHAR(10) NOT NULL,
    analysis_time TIMESTAMP NOT NULL,
    agent_name VARCHAR(50) NOT NULL,
    result_summary TEXT,
    confidence_score DOUBLE PRECISION,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    processing_time_ms BIGINT,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы analyst_reports (наследует от analysis_results)
CREATE TABLE IF NOT EXISTS analyst_reports (
    id BIGINT PRIMARY KEY REFERENCES analysis_results(id) ON DELETE CASCADE,
    market_trend VARCHAR(20),
    technical_indicators TEXT,
    support_level DECIMAL(19,8),
    resistance_level DECIMAL(19,8),
    current_price DECIMAL(19,8),
    price_target DECIMAL(19,8),
    signal_strength VARCHAR(20),
    volume_analysis TEXT,
    momentum_indicators TEXT,
    pattern_recognition TEXT,
    time_horizon_days INTEGER
);

-- Создание таблицы risk_manager_reports (наследует от analysis_results)
CREATE TABLE IF NOT EXISTS risk_manager_reports (
    id BIGINT PRIMARY KEY REFERENCES analysis_results(id) ON DELETE CASCADE,
    risk_level VARCHAR(20) NOT NULL,
    risk_score DECIMAL(5,2),
    volatility_score DECIMAL(5,2),
    value_at_risk DECIMAL(19,8),
    max_drawdown DECIMAL(5,2),
    liquidity_risk DECIMAL(5,2),
    market_cap_risk DECIMAL(5,2),
    regulatory_risk DECIMAL(5,2),
    technical_risk DECIMAL(5,2),
    concentration_risk DECIMAL(5,2),
    beta_coefficient DECIMAL(5,4),
    sharpe_ratio DECIMAL(5,4),
    recommended_position_size DECIMAL(5,2),
    stop_loss_level DECIMAL(19,8),
    correlation_analysis TEXT,
    risk_mitigation_strategies TEXT,
    stress_test_results TEXT
);

-- Создание таблицы trader_reports (наследует от analysis_results)
CREATE TABLE IF NOT EXISTS trader_reports (
    id BIGINT PRIMARY KEY REFERENCES analysis_results(id) ON DELETE CASCADE,
    action_recommendation VARCHAR(20) NOT NULL,
    entry_price DECIMAL(19,8),
    exit_price DECIMAL(19,8),
    stop_loss DECIMAL(19,8),
    take_profit DECIMAL(19,8),
    position_size DECIMAL(19,8),
    risk_reward_ratio DECIMAL(19,8),
    portfolio_allocation DECIMAL(19,8),
    slippage_tolerance DECIMAL(19,8),
    order_type VARCHAR(20),
    time_in_force VARCHAR(20),
    execution_deadline TIMESTAMP,
    execution_strategy TEXT,
    market_timing TEXT,
    trading_rationale TEXT,
    alternative_scenarios TEXT,
    holding_period_days INTEGER,
    urgency_level INTEGER,
    expected_return DECIMAL(19,8)
);

-- Создание индексов для лучшей производительности запросов
CREATE INDEX IF NOT EXISTS idx_analysis_reports_symbol ON analysis_reports(symbol);
CREATE INDEX IF NOT EXISTS idx_analysis_reports_created_at ON analysis_reports(created_at);

CREATE INDEX IF NOT EXISTS idx_analysis_results_ticker ON analysis_results(ticker);
CREATE INDEX IF NOT EXISTS idx_analysis_results_analysis_time ON analysis_results(analysis_time);
CREATE INDEX IF NOT EXISTS idx_analysis_results_agent_name ON analysis_results(agent_name);
CREATE INDEX IF NOT EXISTS idx_analysis_results_status ON analysis_results(status);
CREATE INDEX IF NOT EXISTS idx_analysis_results_confidence_score ON analysis_results(confidence_score);
CREATE INDEX IF NOT EXISTS idx_analysis_results_ticker_time ON analysis_results(ticker, analysis_time);

-- Индексы для отчетов аналитика
CREATE INDEX IF NOT EXISTS idx_analyst_reports_market_trend ON analyst_reports(market_trend);
CREATE INDEX IF NOT EXISTS idx_analyst_reports_signal_strength ON analyst_reports(signal_strength);
CREATE INDEX IF NOT EXISTS idx_analyst_reports_current_price ON analyst_reports(current_price);
CREATE INDEX IF NOT EXISTS idx_analyst_reports_price_target ON analyst_reports(price_target);

-- Индексы для отчетов риск-менеджера
CREATE INDEX IF NOT EXISTS idx_risk_manager_reports_risk_level ON risk_manager_reports(risk_level);
CREATE INDEX IF NOT EXISTS idx_risk_manager_reports_risk_score ON risk_manager_reports(risk_score);
CREATE INDEX IF NOT EXISTS idx_risk_manager_reports_volatility_score ON risk_manager_reports(volatility_score);

-- Индексы для отчетов трейдера
CREATE INDEX IF NOT EXISTS idx_trader_reports_action_recommendation ON trader_reports(action_recommendation);
CREATE INDEX IF NOT EXISTS idx_trader_reports_order_type ON trader_reports(order_type);
CREATE INDEX IF NOT EXISTS idx_trader_reports_execution_deadline ON trader_reports(execution_deadline);
CREATE INDEX IF NOT EXISTS idx_trader_reports_entry_price ON trader_reports(entry_price);

-- Примечание: Функции и триггеры PostgreSQL были бы здесь в продакшене
-- Для совместимости с H2 в тестах мы управляем updated_at вручную в приложении

-- Вставка справочных данных для enum-подобных значений
-- Это помогает с валидацией данных и предоставляет примеры

-- Справочник статусов анализа
-- COMMENT ON COLUMN analysis_results.status IS 'Допустимые значения: PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELLED';

-- Справочник рыночных трендов
-- COMMENT ON COLUMN analyst_reports.market_trend IS 'Допустимые значения: BULLISH, BEARISH, SIDEWAYS, VOLATILE, UNCERTAIN';

-- Справочник силы сигнала
-- COMMENT ON COLUMN analyst_reports.signal_strength IS 'Допустимые значения: VERY_WEAK, WEAK, MODERATE, STRONG, VERY_STRONG';

-- Справочник уровней риска
-- COMMENT ON COLUMN risk_manager_reports.risk_level IS 'Допустимые значения: VERY_LOW, LOW, MODERATE, HIGH, VERY_HIGH, EXTREME';

-- Справочник торговых действий
-- COMMENT ON COLUMN trader_reports.action_recommendation IS 'Допустимые значения: BUY, SELL, HOLD, STRONG_BUY, STRONG_SELL';

-- Справочник типов ордеров
-- COMMENT ON COLUMN trader_reports.order_type IS 'Допустимые значения: MARKET, LIMIT, STOP, STOP_LIMIT, TRAILING_STOP';

-- Справочник времени действия
-- COMMENT ON COLUMN trader_reports.time_in_force IS 'Допустимые значения: DAY, GTC, IOC, FOK, GTD';

-- Добавление комментариев к таблицам для документации (закомментировано для совместимости с H2)
-- COMMENT ON TABLE analysis_reports IS 'Комплексные отчеты анализа, агрегирующие результаты всех агентов';
-- COMMENT ON TABLE analysis_results IS 'Базовая таблица для всех результатов анализа с использованием стратегии JOINED наследования';
-- COMMENT ON TABLE analyst_reports IS 'Отчеты технического анализа, сгенерированные агентом-аналитиком';
-- COMMENT ON TABLE risk_manager_reports IS 'Отчеты оценки рисков, сгенерированные агентом риск-менеджера';
-- COMMENT ON TABLE trader_reports IS 'Торговые рекомендации, сгенерированные агентом-трейдером'; 