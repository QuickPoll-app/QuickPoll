# ============================================
# QuickPoll Data Engineering — Multi-stage Build
# Python 3.11 / ETL Pipeline
# ============================================

# Stage 1: Dependencies
FROM python:3.11-slim AS builder
WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir --prefix=/install -r requirements.txt

# Stage 2: Runtime
FROM python:3.11-slim AS runtime
WORKDIR /app

# Security: non-root user
RUN groupadd -g 1001 appgroup && \
    useradd -u 1001 -g appgroup -s /bin/false appuser && \
    mkdir -p /app/data && \
    chown -R appuser:appgroup /app

# Copy installed dependencies
COPY --from=builder /install /usr/local

# Copy application code
COPY --chown=appuser:appgroup . .

USER appuser

HEALTHCHECK --interval=60s --timeout=10s --start-period=5s --retries=3 \
    CMD python -c "import sys; sys.exit(0)" || exit 1

CMD ["python", "etl_pipeline.py"]
