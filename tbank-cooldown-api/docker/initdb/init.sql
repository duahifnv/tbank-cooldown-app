CREATE TABLE users(
      id UUID PRIMARY KEY,
      nickname VARCHAR(256),
      about TEXT,
      auto_cooling BOOLEAN NOT NULL DEFAULT false,
      banned_categories TEXT[]
);

CREATE TABLE manual_coolings(
      id UUID PRIMARY KEY,
      user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
      min_cost NUMERIC(10, 2) NOT NULL,
      max_cost NUMERIC(10, 2) NOT NULL,
      cooling_timeout INT NOT NULL, -- в днях
      updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE auto_coolings(
      id UUID PRIMARY KEY,
      user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
      month_budget NUMERIC(10, 2) NOT NULL,
      total_savings NUMERIC(10, 2) NOT NULL,
      month_salary NUMERIC(10, 2) NOT NULL,
      updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE purchases(
      id UUID PRIMARY KEY,
      user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
      status VARCHAR(64) NOT NULL,
      name VARCHAR(256) NOT NULL,
      cost NUMERIC(10, 2) NOT NULL,
      category VARCHAR(64),
      purchased_at TIMESTAMP NOT NULL DEFAULT now(),
      cooling_timeout INTEGER
);
