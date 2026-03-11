-- Add updated_at to users if it's missing
DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='updated_at') THEN
        ALTER TABLE users ADD COLUMN updated_at TIMESTAMP;
        -- Initialize with created_at value so we can make it NOT NULL
        UPDATE users SET updated_at = created_at WHERE updated_at IS NULL;
        ALTER TABLE users ALTER COLUMN updated_at SET NOT NULL;
    END IF;
END $$;

-- Add updated_at to polls if it's missing
DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='polls' AND column_name='updated_at') THEN
        ALTER TABLE polls ADD COLUMN updated_at TIMESTAMP;
        -- Initialize with created_at value so we can make it NOT NULL
        UPDATE polls SET updated_at = created_at WHERE updated_at IS NULL;
        ALTER TABLE polls ALTER COLUMN updated_at SET NOT NULL;
    END IF;
END $$;
