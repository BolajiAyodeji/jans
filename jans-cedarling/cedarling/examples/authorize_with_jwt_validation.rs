/*
 * This software is available under the Apache-2.0 license.
 * See https://www.apache.org/licenses/LICENSE-2.0.txt for full text.
 *
 * Copyright (c) 2024, Gluu, Inc.
 */

use cedarling::{
    BootstrapConfig, Cedarling, JwtConfig, LogConfig, LogTypeConfig, PolicyStoreConfig,
    PolicyStoreSource, Request, ResourceData,
};
use std::collections::HashMap;

static POLICY_STORE_RAW: &str = include_str!("../src/init/test_files/policy-store_ok.json");

fn main() -> Result<(), Box<dyn std::error::Error>> {
    // Configure the JwtService to validate signatures using the specified algorithms:
    // `HS256`, `RS256`, and `EC256`.
    //
    // Tokens signed with an algorithm not in `signature_algorithms`
    // will be automatically marked as invalid.
    let jwt_config = JwtConfig::Enabled {
        signature_algorithms: vec![
            "HS256".to_string(),
            "RS256".to_string(),
            "EC256".to_string(),
        ],
    };

    let cedarling = Cedarling::new(BootstrapConfig {
        application_name: "test_app".to_string(),
        log_config: LogConfig {
            log_type: LogTypeConfig::StdOut,
        },
        policy_store_config: PolicyStoreConfig {
            source: PolicyStoreSource::Json(POLICY_STORE_RAW.to_string()),
            store_id: None,
        },
        jwt_config,
    })?;

    let access_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2FkbWluLXVpLXRlc3QuZ2x1dS5vcmciLCJhdWQiOiI1YjQ0ODdjNC04ZGIxLTQwOWQtYTY1My1mOTA3YjgwOTQwMzkiLCJleHAiOjI3MjQ5NDU5NzgsImlhdCI6MTYyNDgzMjI1OX0.At5FSy0vuq_IfRZqIcLSIGH9vYvWJsrnc8fcxDMcCy0".to_string();

    let id_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2FkbWluLXVpLXRlc3QuZ2x1dS5vcmciLCJhdWQiOiI1YjQ0ODdjNC04ZGIxLTQwOWQtYTY1My1mOTA3YjgwOTQwMzkiLCJzdWIiOiJib0c4ZGZjNU1LVG4zN283Z3NkQ2V5cUw4THBXUXRnb080MW0xS1p3ZHEwIiwiZXhwIjoyNzI0OTQ1OTc4LCJpYXQiOjE2MjQ4MzIyNTl9.23IyPqSAhMI7yJuHoLFtXB3Dp1Cr5j3ypwnXtAJhJQ8".to_string();

    let userinfo_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJib0c4ZGZjNU1LVG4zN283Z3NkQ2V5cUw4THBXUXRnb080MW0xS1p3ZHEwIiwiY2xpZW50X2lkIjoiNWI0NDg3YzQtOGRiMS00MDlkLWE2NTMtZjkwN2I4MDk0MDM5IiwibmFtZSI6IkRlZmF1bHQgQWRtaW4gVXNlciIsImVtYWlsIjoiYWRtaW5AZ2x1dS5jb20ifQ.4fIkI6nMR3RRlSM-aYIV21DOO-yZMyRGBCysyLaYwWM".to_string();

    let result = cedarling.authorize(Request {
        access_token,
        id_token,
        userinfo_token,
        action: "Jans::Action::\"Update\"".to_string(),
        context: serde_json::json!({}),
        resource: ResourceData {
            id: "random_id".to_string(),
            resource_type: "Jans::Issue".to_string(),
            payload: HashMap::from_iter([(
                "org_id".to_string(),
                serde_json::Value::String("some_long_id".to_string()),
            )]),
        },
    });
    if let Err(ref e) = &result {
        eprintln!("Error while authorizing: {:?}\n\n", e)
    }

    Ok(())
}
