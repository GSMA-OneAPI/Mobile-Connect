//
//  MobileConnect.h
//  GSMAIOSSDK
//
//  Created by sipbox on 01/07/14.
//  Copyright (c) 2014 Jaco. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AuthorizationOptions.h"
#import "Utils.h"
#import <UIKit/UIKit.h>



/** Formal protocol for discovery API */
//@protocol ProcessAuthorizeDelegate <NSObject>
//@required
/**
// Callback function where the authorize function will return success requested result.
 
// @param statusCode Response code for the authorize request
// @param data Response data object for the authorize request
 
 */
//- (void) authorizeSuccessful:(long)statusCode data:(NSMutableDictionary*)data;

/**
// Callback function where the authorize function will return any error coming of the request.
 
// @param statusCode Result error status code returned by the authorize request. '-1' value is returnet if parsing problems or not status code provided in the response
// @param errorData Response data object for the authorize request
 
// */
//- (void) authorizeError:(long)statusCode data:(NSMutableDictionary*)errorData;

//@end



//Authorization code delegate
/** Formal protocol for Mobile Connect API in processing an authorization request */
@protocol ProcessAuthorizationDelegate <NSObject>

@required


/**
 Callback function where the authorization function will return success requested result.
 
 @param code Authorization code for the request
 @param state State from the request
 
 */
- (void) authorizationSuccessful:(NSString *) code state:(NSString *) state;

/**
 Callback function where the authorization function will return an error
 
 @param error Result error field from the authorization server
 @param errorDescription Description optionally returned from the authorization server
 @param state State from the request
 
 */
- (void) authorizationError:(NSString *)error errorDescription:(NSString *)errorDescription state:(NSString *) state;

@end



//Refresh token delegate
/** Formal protocol for discovery API to catch the refresh token request result */
@protocol ProcessRefreshTokenDelegate <NSObject>

@required


/**
 Callback function where the refreshToken function will return success requested result.
 
 @param statusCode Response code for the refreshToken request
 @param data Response data object for the refreshToken request
 
 */
- (void) refreshTokenSuccessful:(long)statusCode data:(NSMutableDictionary*)data;

/**
 Callback function where the refreshToken function will return any error coming of the request.
 
 @param statusCode Result error status code returned by the refreshToken request. '-1' value is returnet if parsing problems or not status code provided in the response
 @param errorData Response data object for the refreshToken request
 
 */
- (void) refreshTokenError:(long)statusCode data:(NSMutableDictionary*)errorData;

@end




//Revoke token delegate
/** Formal protocol for discovery API to catch the revoke token request result */
@protocol ProcessRevokeTokenDelegate <NSObject>

@required


/**
 Callback function where the revokeToken function will return success requested result.
 
 @param statusCode Response code for the revokeToken request
 @param data Response data object for the revokeToken request
 
 */
- (void) revokeTokenSuccessful:(long)statusCode data:(NSMutableDictionary*)data;

/**
 Callback function where the revokeToken function will return any error coming of the request.
 
 @param statusCode Result error status code returned by the revokeToken request. '-1' value is returnet if parsing problems or not status code provided in the response
 @param errorData Response data object for the revokeToken request
 
 */
- (void) revokeTokenError:(long)statusCode data:(NSMutableDictionary*)errorData;

@end



//Revoking token delegate
/** Formal protocol for discovery API to catch the revoking access token request result */
@protocol ProcessRevokingAccessTokenDelegate <NSObject>

@required


/**
 Callback function where the revokingAccessToken function will return success requested result.
 
 @param statusCode Response code for the revokingAccessToken request
 @param data Response data object for the revokingAccessToken request
 
 */
- (void) revokingAccessTokenSuccessful:(long)statusCode data:(NSMutableDictionary*)data;

/**
 Callback function where the revokingAccessToken function will return any error coming of the request.
 
 @param statusCode Result error status code returned by the revokingAccessToken equest. '-1' value is returnet if parsing problems or not status code provided in the response
 @param errorData Response data object for the revokingAccessToken request
 
 */
- (void) revokingAccessTokenError:(long)statusCode data:(NSMutableDictionary*)errorData;

@end


//Token from authorization code delegate
/** Formal protocol for discovery API to catch the token from authorization code request result */
@protocol ProcessTokenFromAuthorizationCodeDelegate <NSObject>

@required



/**
 Callback function where the tokenFromAuthorizationCode function will return success requested result.
 
 @param statusCode Response code for the tokenFromAuthorizationCode request
 @param access_token Acces token information
 @param expires_in expire information for the token
 @param refresh_token refresh token information
 @param data Response data object for the tokenFromAuthorizationCode request
 
 */
- (void) tokenFromAuthorizationCodeSuccessful:(long)statusCode access_token:(NSString *)access_token expires_in:(NSNumber *)expires_in refresh_token:(NSString *)refresh_token data:(NSMutableDictionary*)data;

/**
 Callback function where the tokenFromAuthorizationCode function will return any error coming of the request.
 
 @param statusCode Result error status code returned by the tokenFromAuthorizationCode request. '-1' value is returnet if parsing problems or not status code provided in the response
 @param errorData Response data object for the tokenFromAuthorizationCode request
 
 */
- (void) tokenFromAuthorizationCodeError:(long)statusCode data:(NSMutableDictionary*)errorData;

@end





//User info delegate
/** Formal protocol for discovery API to catch the user info request result */
@protocol ProcessUserInfoDelegate <NSObject>

@required

/**
 Callback function where the userInfo function will return success requested result.
 
 @param statusCode Response code for the userInfo request
 @param data Response data object for the userInfo request
 
 */
- (void) userInfoSuccessful:(long)statusCode data:(NSMutableDictionary*)data;

/**
 Callback function where the userInfo function will return any error coming of the request.
 
 @param statusCode Result error status code returned by the userInfo request. '-1' value is returnet if parsing problems or not status code provided in the response
 @param errorData Response data object for the userInfo request
 
 */
- (void) userInfoError:(long)statusCode data:(NSMutableDictionary*)errorData;

@end







/**
 Class that provide functions for using the MobileConnect API.
 */
@interface MobileConnect : NSObject<UIWebViewDelegate>
{
    //Discovery delegate obj
    id <ProcessRefreshTokenDelegate> refreshTokenDelegate;
    id <ProcessRevokeTokenDelegate> revokeTokenDelegate;
    id <ProcessRevokingAccessTokenDelegate> revokingAccessTokenDelegate;
    id <ProcessTokenFromAuthorizationCodeDelegate> tokenFromAuthorizationCodeDelegate;
    id <ProcessUserInfoDelegate> userInfoDelegate;
    id <ProcessAuthorizationDelegate> authorizationDelegate;
    
    
    //Utils object for internal workarounds
    Utils* utils;
}

@property BOOL traceMode;

/** Property definition for MobileConnect
 @property refresh token delegate definition for managing the async function response
 */
@property id refreshTokenDelegate;

/** Property definition for MobileConnect
 @property revoke token delegate definition for managing the async function response
 */
@property id revokeTokenDelegate;

/** Property definition for MobileConnect
 @property revoking access delegate definition for managing the async function response
 */
@property id revokingAccessTokenDelegate;

/** Property definition for MobileConnect
 @property token form authorization code delegate definition for managing the async function response
 */
@property id tokenFromAuthorizationCodeDelegate;

/** Property definition for MobileConnect
 @property user info delegate definition for managing the async function response
 */
@property id userInfoDelegate;

/** Property definition for MobileConnect
 @property authorization delegate definition for managing the async function response
 */
@property id authorizationDelegate;

typedef enum{
    NONE = 1,
    LOGIN,
    CONSENT,
    SELECT_ACCOUNT
    
} promptEnumType;

/** Property definition for redirect uri
 @property redirectUri used for functions
 */
@property (strong, nonatomic) NSString *redirectUri;

/**
 Authorize request
 
 @param url request url location
 @param clientID Needed for OAuth 2.0 authorisation request.
 @param scope Space delimited and case-sensitive list of ASCII strings for OAuth 2.0 scope values. OIDC Authorisation request MUST contain the scope value “openid”. The other optional values for scope in OIDC are: “profile”, “email”, “address”, “phone” and “offline_access”.
 @param redirectUri The URI where the response will be sent through redirection. The URI MUST match one of the pre-registered redirect_uris at client registration/provisioning.
 @param responseType The value MUST be “code”, to indicate that the grant type flow to be used is Authorisation Code. It also indicates that the access_token (and id_token) be returned in exchange of “code”.
 @param state Value used by the client to maintain state between request and callback. A security mechanism as well, if a cryptographic binding is done with the browser cookie, to prevent Cross-Site Request Forgery.
 @param nonce String value used to associate a client session with the ID Token. It is passed unmodified from Authorisation Request to ID Token. The value SHOULD be unique per session to mitigate replay attacks.
 @param prompt String velues to specify to the authorization sever
 @param maxAge Specifies the maximum elapsed time in seconds since last authentication of the user. If the elapsed time is greater than this value, a reauthentication MUST be done. When this parameter is used in the request, the ID Token MUST contain the auth_time claim value.
 @param acrValues Authentication Context class Reference. Space separated string that specifies the Authentication Context Reference to be used during authentication processing. The LOA required by the RP/Client for the use case can be used here. The values appear as order of preference. The acr satisfied during authentication is returned as acr claim value.
 The recommended values are the LOAs as specified in ISO/IEC 29115 Clause 6 – 1, 2, 3, 4 – representing the LOAs of LOW, MEDIUM, HIGH and VERY HIGH.
 The acr_values are indication of what authentication method to used by the IDP. The authentication methods to be used are linked to the LOA value passed in the acr_values. The IDP configures the authentication method selection logic based on the acr_values.
 @param authorizationOptions Object representation of the display, ui_locales, claims_locales, id_token_hint, login_hint and dtbs.
 @param clientSecret which is secret for the application
 */
-(void)authorize:(NSString *)url clientID:(NSString *)clientID clientSecret:(NSString *)clientSecret scope:(NSString *)scope redirectUri:(NSString *)redirectUri responseType:(NSString *)responseType state:(NSString *)state nonce:(NSString *)nonce prompt:(promptEnumType)prompt maxAge:(int)maxAge acrValues:(NSString *)acrValues authorizationOptions:(AuthorizationOptions *)authorizationOptions webview:(UIWebView *) webview;


/**
 Refresh an existing token with an expiration time
 
  @param url request url location
 @param scope Space delimited and case-sensitive list of ASCII strings for OAuth 2.0 scope values. OIDC Authorisation request MUST contain the scope value “openid”. The other optional values for scope in OIDC are: “profile”, “email”, “address”, “phone” and “offline_access”.
 @param clientID Needed for OAuth 2.0 authorisation request.
 @param clientSecret The client_secret used in HTTP Basic Authentication using the OAuth 2.0 Client Password mechanism [RFC 6749 Section 2.3.1]
 @param refreshToken OAuth 2.0 referesh token to get the access_token when the access_token expires.
 */
-(void)refreshToken:(NSString *)url refreshToken:(NSString*)refreshToken  scope:(NSString*)scope clientID:(NSString*)clientID clientSecret:(NSString *)clientSecret;

/**
 Revoke an existing token
 
  @param url request url location
 @param clientID Needed for OAuth 2.0 authorisation request.
 @param clientSecret The client_secret used in HTTP Basic Authentication using the OAuth 2.0 Client Password mechanism [RFC 6749 Section 2.3.1]
 
 */
-(void)revokeToken:(NSString*)url clientID:(NSString*)clientID clientSecret:(NSString *)clientSecret;

/**
 Revoke an existing access token
 
  @param url request url location
 @param clientID Needed for OAuth 2.0 authorisation request.
 @param clientSecret The client_secret used in HTTP Basic Authentication using the OAuth 2.0 Client Password mechanism [RFC 6749 Section 2.3.1]
 @param accessToken OAuth 2.0 referesh token to get the access_token when the access_token expires.
 
 */
-(void)revokingAccessToken:(NSString*)url accessToken:(NSString*)accessToken clientID:(NSString*)clientID clientSecret:(NSString *)clientSecret;

/**
 Parse the authorization code and get the token
 
 @param url request url location
 @param code The authorisation code received from the authorisation server, from the authorisation request
 @param clientID Needed for OAuth 2.0 authorisation request.
 @param clientSecret The client_secret used in HTTP Basic Authentication using the OAuth 2.0 Client Password mechanism [RFC 6749 Section 2.3.1]
 @param redirectUri The URI where the response will be sent through redirection. The URI MUST match one of the pre-registered redirect_uris at client registration/provisioning.
 */
-(void)tokenFromAuthorizationCode:(NSString*)url code:(NSString*)code clientID:(NSString*)clientID clientSecret:(NSString*)clientSecret redirectUri:(NSString*)redirectUri;

/**
    Get user info
 
 @param url request url location
 @param accessToken OAuth 2.0 access_token, used to get the UserInfo object from the UserInfo end-point and can be reused for accessing other protected resources, if required.
 */
-(void)userInfo:(NSString*)url accessToken:(NSString*)accessToken;


/**
 
 Is access token valid
 
 @param timestamp Timestamp to campare with the current time
 @return boolean with the result. 'YES' if access token is valid, 'NO' otherwise
*/
-(BOOL)isAccessTokenValid:(NSTimeInterval*)timestamp;


/** 
 This function extract the parameters within a provided url
 @param url Provided url containing one or more parameters to parse (code, error_description, error_code, access_token, expires_in
 @return NSMutableDictionary representation of the result. Nil if parse error ocurred or empty NSMutableDictionary if non parameters to parse
 */
- (NSMutableDictionary*)extractRedirectParameter: (NSString*) url;

@end
