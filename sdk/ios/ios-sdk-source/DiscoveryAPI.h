//
//  DiscoveryAPI.h
//  GSMAIOSSDK
//
//  Created by Hugo Galan on 18/06/14.
//  Copyright (c) 2014 Solaiemes S.L. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Utils.h"
#import <UIKit/UIKit.h>


/** Formal protocol for discovery API */
@protocol ProcessGetDiscoveryResponseDelegate <NSObject>
@required

/**
 Callback function where the getDiscoveryActive/Pasive function will return success requested result.
 
 @param statusCode Response code for the getDiscovery request
 @param data Response data object for the getDiscovery request
 
 */
- (void) getDiscoverySuccessful:(long)statusCode data:(NSMutableDictionary*)data;

/**
 Callback function where the getDiscoveryActive/Pasive function will return any error coming of the request.
 
 @param statusCode Result error status code returned by the getDiscoveryActive/Pasive request. '-1' value is returnet if parsing problems or not status code provided in the response
 @param errorData Response data object for the getDiscovery request
 
 */
- (void) getDiscoveryError:(long)statusCode data:(NSMutableDictionary*)errorData;
@end


/** 
 Class that provide functions for using the Discovery API.
 */
@interface DiscoveryAPI : NSObject<UIWebViewDelegate>
{
    //Discovery delegate obj
    id <ProcessGetDiscoveryResponseDelegate> discoveryDelegate;
    
    //Utils object for internal workarounds
    Utils* utils;
}

@property BOOL traceMode;

/** Delegate definitions */

/** Property definition for Discovery delegate
 
 @property Discovery delegate definition
 */
@property  id discoveryDelegate;

@property (strong, nonatomic) NSString *redirectUri;
@property (strong, nonatomic) NSString *clientId;
@property (strong, nonatomic) NSString *clientSecret;
@property (strong, nonatomic) NSString *discoveryurl;
@property encryptionTypeEnum encryption;

/** Discovery API functions */


/** Make the get discovery request
 *  Using auto mcc - mnc
 *
 
 @param url Destination url location where the request will make
 @param clientId Application client id provided for the authentication
 @param clientSecret Application client secret (password)provided for the authentication
 @param encryption Encrytation type for the authentication header. Types are: 'encryptionTypeEnumBasic', 'encryptionTypeEnumSha-256', 'encryptionTypeEnumNone'.
 @param ipAddress Provided ip addres for the 'x-source-ip' header in the request
 @param redirectUri Specifies the location which continues the discovery process (optional)
 @param msisdn param if provided (optional)
 @param webview Specifies the webview created by the application used if required to display the operator selection screen
 */
- (void)getDiscoveryActive: (NSString*)url clientId:(NSString*)clientId clientSecret:(NSString*)clientSecret encryption:(encryptionTypeEnum)encryption ipAddress:(NSString*)ipAddress redirectUri:(NSString*)redirectUri msisdn:(NSString*)msisdn webview:(UIWebView *) webview;


/** Make the get discovery request
 *  Using auto mcc - mnc
 *
 
 @param url Destination url location where the request will make
 @param clientId Application client id provided for the authentication
 @param clientSecret Application client secret (password)provided for the authentication
 @param encryption Encryption type for the authentication header. Types are: 'encryptionTypeEnumBasic', 'encryptionTypeEnumSha-256', 'encryptionTypeEnumNone'.
 @param ipAddress Provided ip addres for the 'x-source-ip' header in the request
 @param redirectUri Specifies the location which continues the discovery process (optional)
 @param msisdn param if provided (optional)
 */
- (void)getDiscoveryPassive: (NSString*)url clientId:(NSString*)clientId clientSecret:(NSString*)clientSecret encryption:(encryptionTypeEnum)encryption ipAddress:(NSString*)ipAddress  redirectUri:(NSString*)redirectUri msisdn:(NSString*)msisdn;




/** Make the get discovery request
 *  Using provided mcc - mnc
 *
 
 @param url Destination url location where the request will make
 @param clientId Application client id provided for the authentication
 @param clientSecret Application client secret (password)provided for the authentication
 @param encryption Encryption type for the authentication header. Types are: 'encryptionTypeEnumBasic', 'encryptionTypeEnumSha-256', 'encryptionTypeEnumNone'.
 @param ipAddress Provided ip addres for the 'x-source-ip' header in the request
 @param redirectUri Specifies the location which continues the discovery process (optional)
 @param msisdn param if provided (optional)
 @param mcc Mobile Country Code provided
 @param mnc Mobile Network Code provided
 @param webview Specifies the webview created by the application used if required to display the operator selection screen
 */
- (void)getDiscoveryActive: (NSString*)url clientId:(NSString*)clientId clientSecret:(NSString*)clientSecret encryption:(encryptionTypeEnum)encryption ipAddress:(NSString*)ipAddress redirectUri:(NSString*)redirectUri msisdn:(NSString*)msisdn mcc:(NSString*)mcc mnc:(NSString*)mnc  webview:(UIWebView *) webview;


/** Make the get discovery request
 *  Using provided mcc - mnc
 *
 
 @param url Destination url location where the request will make
 @param clientId Application client id provided for the authentication
 @param clientSecret Application client secret (password)provided for the authentication
 @param encryption Encryption type for the authentication header. Types are: 'encryptionTypeEnumBasic', 'encryptionTypeEnumSha-256', 'encryptionTypeEnumNone'.
 @param ipAddress Provided ip addres for the 'x-source-ip' header in the request
 @param redirectUri Specifies the location which continues the discovery process (optional)
 @param msisdn param if provided (optional)
 @param mcc Mobile Country Code provided
 @param mnc Mobile Network Code provided
 */
- (void)getDiscoveryPassive: (NSString*)url clientId:(NSString*)clientId clientSecret:(NSString*)clientSecret encryption:(encryptionTypeEnum)encryption ipAddress:(NSString*)ipAddress  redirectUri:(NSString*)redirectUri msisdn:(NSString*)msisdn  mcc:(NSString*)mcc mnc:(NSString*)mnc;



/** This function return the last stored item representation
 @return Object representation of the result stored in the last success getDiscoveryActive/getDiscoveryPasive request made.
 */
- (NSMutableDictionary*)getCachedDiscoveryItem;

/**
 This function clear the logo item stored in the last successfull call to the getDiscoveryActive/getDiscoveryPasive function. This function clear too the last MCC MNC used and stored in application cache
 @return Boolean representation of the result
 */
- (BOOL)clearCachedDiscoveryItem;


/**
 This function extract the mnc mcc of an provided url
 @param url Provided url containing the mcc, mnc
 @return NSMutableDictionary representation of mcc_mnc parameter. Nil if parse error ocurred.
 */
- (NSMutableDictionary*)extractRedirectParameter: (NSString*) url;



/**
 This function extract a field String from the Discovery NSMutableDiscovery result
 @param discoveryData data to extract a field
 @param field to look for it in the discovery data
 @return NSString value of the searched field in the discovery data
 */
- (NSString *) responseField: (NSMutableDictionary *) discoveryData field: (NSString *) field;

/**
 This function extract information of a discovery object
 @param discoveryData response data obtained with the discovery request
 @param api param to match in the discovery data
 @param function param to match in the discovery data
 @return endpoint NSString field match with the provided api and function parameters in the discoveryData object
 */
- (NSString *)endpoint: (NSMutableDictionary *)discoveryData api:(NSString *)api function:(NSString *)function;


@end
