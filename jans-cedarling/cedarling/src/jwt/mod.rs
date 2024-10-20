/*
 * This software is available under the Apache-2.0 license.
 * See https://www.apache.org/licenses/LICENSE-2.0.txt for full text.
 *
 * Copyright (c) 2024, Gluu, Inc.
 */

//! # `JwtEngine`
//!
//! The `JwtEngine` is designed for managing JSON Web Tokens (JWTs) and provides the following functionalities:
//! - Fetching decoding keys from a JSON Web Key Set (JWKS) provided by Identity Providers (IDPs) and storing these keys.
//! - Extracting claims from JWTs for further processing and validation.
//! - Validating the signatures of JWTs to ensure their integrity and authenticity.
//! - Verifying the validity of JWTs based on claims such as expiration time and audience.

mod decoding_strategy;
mod error;
#[cfg(test)]
mod test;
mod token;

use decoding_strategy::*;
pub use error::*;
use token::*;

use serde::de::DeserializeOwned;

use crate::JwtConfig;

pub struct JwtService {
    decoding_strategy: DecodingStrategy,
}

/// A service for handling JSON Web Tokens (JWT).
///
/// The `JwtService` struct provides functionality to decode and optionally validate
/// JWTs based on a specified decoding strategy. It can be configured to either
/// perform validation or to decode without validation, depending on the provided
/// configuration. It is an internal module used by other components of the library.
impl JwtService {
    /// Creates a new instance of `JwtService` for testing purposes.
    ///
    /// This constructor allows for the injection of a specific decoding strategy,
    /// facilitating unit testing by simulating different decoding and validation scenarios.
    /// It is useful for testing both successful and failing cases for various token types.
    #[cfg(test)]
    pub fn new(decoding_strategy: DecodingStrategy) -> Self {
        Self { decoding_strategy }
    }

    /// Initializes a new `JwtService` instance based on the provided configuration.
    ///
    /// This method creates a `JwtService` instance, which can either validate JWTs or
    /// simply decode them without validation, depending on the provided `JwtConfig`.
    /// It uses a dependency map to retrieve necessary services like key services.
    ///
    /// # Parameters
    /// - `dep_map`: A dependency map providing required services.
    /// - `config`: Configuration specifying whether JWT validation should be enabled.
    ///
    /// # Errors
    /// Returns an error if any issue arises while setting up the decoding strategy.
    pub fn new_with_container(
        dep_map: &di::DependencyMap,
        config: JwtConfig,
    ) -> Result<Self, Error> {
        match config {
            JwtConfig::Disabled => {
                let decoding_strategy = DecodingStrategy::new_without_validation();
                Ok(Self { decoding_strategy })
            },
            JwtConfig::Enabled {
                signature_algorithms,
            } => {
                let decoding_strategy =
                    DecodingStrategy::new_with_validation(dep_map, &signature_algorithms)?;

                Ok(Self { decoding_strategy })
            },
        }
    }

    /// Decodes and validates both an `access_token` and an `id_token`.
    ///
    /// This method decodes both tokens, validates them according to the internal
    /// `DecodingStrategy`, and enforces token relationships, ensuring that the
    /// `id_token` is validated against claims from the `access_token`.
    ///
    /// # Token Validation Rules:
    /// 1. The `access_token` is decoded and validated first, with its `aud` (which is also the `client_id`)
    ///    stored for later use.
    /// 2. The `id_token` is then validated against the `access_token.aud` (client_id) and `access_token.iss` (issuer).
    /// 3. An error is returned if `id_token.aud` does not match `access_token.client_id`.
    ///
    /// # Parameters
    /// - `access_token_str`: The JWT string representing the access token.
    /// - `id_token_str`: The JWT string representing the ID token.
    ///
    /// # Returns
    /// A tuple containing the decoded claims for the `access_token` and `id_token`.
    ///
    /// # Errors
    /// Returns an error if decoding or validation of either token fails.
    pub fn decode_tokens<A, I>(
        &self,
        access_token_str: &str,
        id_token_str: &str,
    ) -> Result<(A, I), Error>
    where
        A: DeserializeOwned,
        I: DeserializeOwned,
    {
        // extract claims without validation
        let access_token_claims = self.decoding_strategy.extract_claims(access_token_str)?;
        let id_token_claims = self.decoding_strategy.extract_claims(id_token_str)?;

        // validate access_token
        let access_token = self.decoding_strategy.decode::<AccessToken>(
            access_token_str,
            None::<String>, // TODO: validate issuer for access token
            None::<String>,
            false,
        )?;

        // validate the id_token against the access_token's `iss` and `aud`
        self.decoding_strategy.decode::<IdToken>(
            id_token_str,
            Some(&access_token.iss),
            Some(&access_token.aud),
            true,
        )?;

        Ok((access_token_claims, id_token_claims))
    }
}
