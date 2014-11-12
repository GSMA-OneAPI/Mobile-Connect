//
//  Utils.h
//  GSMAIOSSDK
//
//  Created by Hugo Galan on 30/06/14.
//  Copyright (c) 2014 Jaco. All rights reserved.
//

#import <Foundation/Foundation.h>

//MCC MNC
#import <CoreTelephony/CTTelephonyNetworkInfo.h>
#import <CoreTelephony/CTCarrier.h>

@interface Utils : NSObject
{
    
    //Define user default info object for persistent data
    NSUserDefaults *userDefInfo ;
    
}

@property BOOL traceMode;

/**
 encryption option values
 */
typedef enum{
    /** Sha-256 encryption */
    encryptionTypeEnumSHA256 = 1, /** Sha-256 codification */
    //encryptionTypeEnumSHA1, /** Sha-1 codification */
    /** Basic encryption */
    encryptionTypeEnumBASIC, /** Base64 codification */
    /** Non encryption, credentials won't be sent */
    encryptionTypeEnumNONE
} encryptionTypeEnum;

/**
 logo size values
 */
typedef enum{
    logoSizeTypeEnumSMALL = 1,
    logoSizeTypeEnumMEDIUM,
    logoSizeTypeEnumLARGE
} logoSizeTypeEnum;

/**
 background color option values
 */
typedef enum{
    bg_colorTypeEnumBLACK = 1,
    bg_colorTypeEnumNORMAL,
    bg_colorTypeEnumREVERSED
} bg_colorTypeEnum;

/**
 Aspect ratio option values
 */
typedef enum{
    aspect_ratioTypeEnumSQUARE = 1,
    aspect_ratioTypeEnumLANDSCAPE
} aspect_ratioTypeEnum;

/**
 Api type option values
 */
typedef enum{
    apiTypeEnumPAYMENT = 1,
    apiTypeEnumOPERATORID
} apiTypeEnum;



-init;

+ (NSString*)getEncryption:(encryptionTypeEnum)encryption client:(NSString*)client key:(NSString*)key traceMode:(BOOL) traceMode;

// - - SHA-256 SHA-1 codification - -
+ (NSString *)doBasic:(NSString *)stringData traceMode:(BOOL) traceMode;

// - - SHA-256 SHA-1 codification - -
+ (NSString *)doSha1:(NSString *)stringData traceMode:(BOOL) traceMode;

+ (NSString *)doSha256:(NSString *)stringData traceMode:(BOOL) traceMode;






// - - OBTAIN MCC - MNC FROM PHONE - -

- (NSString*)getMCC;

- (NSString*)getMNC;



// - - PERSISTENT STORAGE - -


//GET LAST DISCOVERY/LOGO RESPONSES
- (NSMutableDictionary*)getLastDiscoveryResponse;
- (NSArray*)getLastLogoResponse;

//SAVE LAST DISCOVERY/LOGO RESPONSES
- (void)saveLastDiscoveryResponse:(NSMutableDictionary*)response;
- (void)saveLastLogoResponse:(NSArray*)response;

//CLEAR LAST DISCOVERY/LOGO RESPONSES
- (BOOL)clearLastDiscoveryResponse ;
- (BOOL)clearLastLogoResponse;

//GET MCC MNC
- (NSString*)getCachedMCC;
- (NSString*)getCachedMNC;

//SAVE MCC MNC
- (void)saveMCCtoCache:(NSString*)mcc;
- (void)saveMNCtoCache:(NSString*)mnc;

//CLEAR MCC MNC
- (BOOL)clearMobileNetwork_MCC_MNC;


//  URL ENCODER UTIL
+ (NSString *)urlEncode:(NSString *)source;
+ (NSString*)nameForSize:(logoSizeTypeEnum)size;
//+ (NSString*)nameForApi:(apiTypeEnum)api;
+ (NSString*)nameForBg_color:(bg_colorTypeEnum)bg_color;
+ (NSString*)nameForAspectRatio:(aspect_ratioTypeEnum)aspectRatio;


@end
