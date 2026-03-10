# ============================================
# QuickPoll Data Engineering — Multi-stage Build
# Python 3.11 / ETL Pipeline
# ============================================
# syntax=docker/dockerfile:1

# Build Args
ARG PYTHON_VERSION=3.11-slim

# Stage 1: Dependencies (cached layer)
FROM python:${PYTHON_VERSION} AS builder
WORKDIR /build

# Install build-time deps for compiled packages (psycopg2-binary, etc.)
RUN apt-get update && \
    apt-get install -y --no-install-recommends gcc libpq-dev && \
    rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN --mount=type=cache,target=/root/.cache/pip \
    pip install --no-cache-dir --prefix=/install -r requirements.txt

# Stage 2: Runtime
FROM python:${PYTHON_VERSION} AS runtime

# OCI image labels
LABEL org.opencontainers.image.title="QuickPoll Data Engineering" \
    org.opencontainers.image.description="ETL Pipeline for QuickPoll analytics" \
    org.opencontainers.image.source="https://github.com/QuickPoll-app/QuickPoll"

WORKDIR /app

# Install runtime-only libs + tini for PID 1 handling, create non-root user
RUN apt-get update && \
    apt-get install -y --no-install-recommends libpq5 tini && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd -g 1001 appgroup && \
    useradd -u 1001 -g appgroup -s /bin/false appuser && \
    mkdir -p /app/data && \
    chown -R appuser:appgroup /app

# Copy installed dependencies
COPY --from=builder /install /usr/local

# Copy application code
COPY --chown=appuser:appgroup . .

# Drop to non-root
USER appuser:appgroup

# Python hardening
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

HEALTHCHECK --interval=60s --timeout=10s --start-period=10s --retries=3 \
    CMD python -c "import pandas; import sqlalchemy; print('ok')" || exit 1

# tini as init — proper signal forwarding & zombie reaping
ENTRYPOINT ["tini", "--"]
CMD ["python", "etl_pipeline.py"]
