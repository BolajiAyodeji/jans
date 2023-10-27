//
//  DPoPProofFactory.swift
//  Jans-Chip
//
//  Created by Nazar Yavornytskyi on 27.10.2023.
//

import Foundation
import SwiftJWT

struct JansClaims: Claims {
    let jti: String
    let htm: String
    let htu: String
    let iat: Date
}

final class DPoPProofFactory {
    
    static let shared = DPoPProofFactory()
    
    private init() {}
    
    /**
         * The function `issueDPoPJWTToken` generates a DPoP (Distributed Proof of Possession) JWT (JSON Web Token) with the
         * specified HTTP method and request URL.
         *
         * @param httpMethod The `httpMethod` parameter represents the HTTP method used in the request, such as "GET", "POST",
         *                   "PUT", etc.
         * @param requestUrl The `requestUrl` parameter is the URL of the HTTP request that you want to issue a DPoP JWT token
         *                   for. It represents the target resource or endpoint that you want to access.
         * @return The method is returning a JWT (JSON Web Token) token.
     */
    public func issueDPoPJWTToken(httpMethod: String, requestUrl: String) -> String {
        // KeyManager.getPublicKeyJWK(KeyManager.getInstance().getPublicKey()).getRequiredParams()
        // The "alg" header will be set to the algorithm name when you sign the JWT
        
        var tokenJWT = ""
        guard let localHeimdall = Heimdall(tagPrefix: "com.jans.chip.ios") else {
            return tokenJWT
        }
        
        let header = Header(typ: "dpop+jwt", jwk: localHeimdall.publicKeyData()?.base64EncodedString() ?? "")
        let claims = JansClaims(
            jti: UUID().uuidString,
            htm: httpMethod,
            htu: requestUrl,
            iat: Date()
        )
        
        var objectJWT = JWT(header: header, claims: claims)
        
        guard let privateKeyData = localHeimdall.privateKeyData() else {
            return tokenJWT
        }
        
        let jwtSigner = JWTSigner.rs256(privateKey: privateKeyData)
        do {
            tokenJWT = try objectJWT.sign(using: jwtSigner)
        } catch(let error) {
            print("Error generating JWT, reason: \(error.localizedDescription)")
        }
        
        return tokenJWT
    }
}
