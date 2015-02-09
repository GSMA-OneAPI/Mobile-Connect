//
//  Utils.m
//  GSMAIOSSDK
//
//  Created by Hugo Galan on 30/06/14.
//  Copyright (c) 2014 Jaco. All rights reserved.
//

#import "Utils.h"

//CRYPTO LIB for SHA1-SHA256
#import <CommonCrypto/CommonDigest.h>
#import <CommonCrypto/CommonCryptor.h>
#import <CommonCrypto/CommonHMAC.h>


@implementation Utils



- init{
    
    userDefInfo = [NSUserDefaults standardUserDefaults];
    
	return [super init];
}

/** @name Encryption utils */

/**
 
 @param encryptionType
 @param client
 @param key
 @param traceMode
 */
+ (NSString*)getEncryption:(encryptionTypeEnum)encryption client:(NSString*)client key:(NSString*)key traceMode:(BOOL) traceMode {
    
    // Ensure that a missing key is converted to an empty string
    if (key==nil) key=@"";
    
    //sha-256 authentication
    if (encryption == encryptionTypeEnumSHA256) {
        
        if (traceMode) NSLog(@"Encoding using sha-256 authentication");
        NSString *authStr = [NSString stringWithFormat:@"%@:%@", client, key ];
        return [self doSha256:authStr traceMode:traceMode];
        
    }else if (encryption == encryptionTypeEnumBASIC) {//Basic authentication
        
        if (traceMode) NSLog(@"Encoding using Basic authentication");
        NSString *authStr = [NSString stringWithFormat:@"%@:%@", client, key ];
        return [self doBasic:authStr traceMode:traceMode];
        
    }else if (encryption == encryptionTypeEnumNONE) {//No authentication
        if (traceMode) NSLog(@"Encoding using NONE authentication. Non-credential case.");
        return @"";
    }else{
        if (traceMode) NSLog(@"Encoding using PLAIN authentication");
        return [NSString stringWithFormat:@"%@:%@", client, key ];
    }
}

// - - BASIC codification - -
+ (NSString *)doBasic:(NSString *)stringData traceMode:(BOOL) traceMode{
    
    NSData *authData = [stringData dataUsingEncoding:NSUTF8StringEncoding];
    NSString *base64EncodedString = [authData base64EncodedStringWithOptions:0];
    if (traceMode) NSLog(@"Base64String: Basic %@",base64EncodedString);
    
    return base64EncodedString;
    
}

// - - SHA-256 SHA-1 codification - -
+ (NSString *)doSha1:(NSString *)stringData traceMode:(BOOL) traceMode{
    
    NSData* dataIn = [stringData dataUsingEncoding:NSUTF8StringEncoding];
    dataIn = [dataIn subdataWithRange:NSMakeRange(0, [dataIn length] - 1)];
    
    NSMutableData *macOut = [NSMutableData dataWithLength:CC_SHA1_DIGEST_LENGTH];
    
    CC_SHA1( dataIn.bytes,
            (int) dataIn.length,
            macOut.mutableBytes);
    
    //Hashed with spaces and brackets
    NSString *someString = [NSString stringWithFormat:@"%@", macOut];
    if (traceMode) NSLog(@"Somestring sha-1: %@",someString);
    
    //spaces and brackets removed
    NSCharacterSet *charsToRemove = [NSCharacterSet characterSetWithCharactersInString:@"< >"];
    someString = [[macOut description] stringByTrimmingCharactersInSet:charsToRemove];
    if (traceMode) NSLog(@"Somestring sha-1: %@",someString);
    
    return [[NSString alloc] initWithData:macOut encoding:NSUTF8StringEncoding];
}

+ (NSString *)doSha256:(NSString *)stringData traceMode:(BOOL) traceMode{
    
    NSData* dataIn = [stringData dataUsingEncoding:NSUTF8StringEncoding];
    dataIn = [dataIn subdataWithRange:NSMakeRange(0, [dataIn length] - 1)];
    
    NSMutableData *macOut = [NSMutableData dataWithLength:CC_SHA256_DIGEST_LENGTH];
    
    CC_SHA256( dataIn.bytes,
              (int) dataIn.length,
              macOut.mutableBytes);
    
    //Hashed with spaces and brackets
    NSString *someString = [NSString stringWithFormat:@"%@", macOut];
    if (traceMode) NSLog(@"Somestring sha-256: %@",someString);
    
    //spaces and brackets removed
    NSCharacterSet *charsToRemove = [NSCharacterSet characterSetWithCharactersInString:@"< >"];
    someString = [[macOut description] stringByTrimmingCharactersInSet:charsToRemove];
    if (traceMode) NSLog(@"Somestring sha-256: %@",someString);
    
    return [[NSString alloc] initWithData:macOut encoding:NSUTF8StringEncoding];
}






// - - OBTAIN MCC - MNC FROM PHONE - -

- (NSString*)getMCC{
    CTTelephonyNetworkInfo *netInfo = [[CTTelephonyNetworkInfo alloc] init];
    CTCarrier *carrier = [netInfo subscriberCellularProvider];
    //NSLog(@"CoreTelephony: \n-MCC: %@",[carrier mobileCountryCode]);
    
    if (![carrier mobileCountryCode]) {
        return nil;
    }else{
        return [carrier mobileCountryCode];
    }
}

- (NSString*)getMNC{
    CTTelephonyNetworkInfo *netInfo = [[CTTelephonyNetworkInfo alloc] init];
    CTCarrier *carrier = [netInfo subscriberCellularProvider];
    //NSLog(@"CoreTelephony: \n-MNC: %@",[carrier mobileNetworkCode]);
    
    if (![carrier mobileNetworkCode]) {
        return nil;
    }else{
        return [carrier mobileNetworkCode];
    }
}




// - - PERSISTENT STORAGE - -


//GET LAST DISCOVERY/LOGO RESPONSES
- (NSMutableDictionary*)getLastDiscoveryResponse {
    
    NSMutableDictionary *storedDictionary = [userDefInfo objectForKey:@"lastDiscoveryResponse"];
    if (_traceMode) NSLog(@"Getting discovery data stored: \n%@", storedDictionary);
    
    return storedDictionary;
}

- (NSArray*)getLastLogoResponse {
    
    NSArray *storedDictionary = [userDefInfo  arrayForKey:@"lastLogoResponse"];
    
    if (_traceMode) NSLog(@"Getting logo data stored: \n%@", storedDictionary);
    
    return storedDictionary;
}

//SAVE LAST DISCOVERY/LOGO RESPONSES
- (void)saveLastDiscoveryResponse:(NSMutableDictionary*)response {
    
    if (userDefInfo) {
        [NSUserDefaults standardUserDefaults];
    }
    
    if (_traceMode)     NSLog(@"Saving discovery data...");
    if (response) {
        [userDefInfo setObject:response forKey:@"lastDiscoveryResponse"];
        //NSLog(@"Data saved to persistent storage success. Stored data: \n%@", response);
    }
}
- (void)saveLastLogoResponse:(NSArray*)response {
    
    if (userDefInfo) {
        [NSUserDefaults standardUserDefaults];
    }
    
    if (_traceMode) NSLog(@"Saving logo data...");
    if (response) {
        [userDefInfo setObject:response forKey:@"lastLogoResponse"];
        //NSLog(@"Data saved to persistent storage success. Stored data: \n%@", response);
    }
}


//CLEAR LAST DISCOVERY/LOGO RESPONSES
- (BOOL)clearLastDiscoveryResponse {
    
    if (userDefInfo) {
        [NSUserDefaults standardUserDefaults];
    }
    
    //Remove object
    [userDefInfo removeObjectForKey:@"lastDiscoveryResponse"];
    
    //Still the object stored?
    if ([self getLastDiscoveryResponse]) {
        return NO;//Fail removing
    }else{
        return YES;//Sucess removing
    }
}
- (BOOL)clearLastLogoResponse {
    
    if (userDefInfo) {
        [NSUserDefaults standardUserDefaults];
    }
    
    //Remove object
    [userDefInfo removeObjectForKey:@"lastLogoResponse"];
    
    //Still the object stored?
    if ([self getLastLogoResponse]) {
        return NO;//Fail removing
    }else{
        return YES;//Sucess removing
    }
}


//GET MCC MNC
- (NSString*)getCachedMCC{
    
    if (userDefInfo) {
        [NSUserDefaults standardUserDefaults];
    }
    
    NSString *storedMcc = [userDefInfo objectForKey:@"lastMCC"];
    if (_traceMode) NSLog(@"Getting data stored - Last MCC: \n%@", storedMcc);
    
    return storedMcc;
}
- (NSString*)getCachedMNC {
    
    if (userDefInfo) {
        [NSUserDefaults standardUserDefaults];
    }
    
    NSString *storedMnc = [userDefInfo objectForKey:@"lastMNC"];
    if (_traceMode) NSLog(@"Getting data stored - Last MNC: \n%@", storedMnc);
    
    return storedMnc;
}

//SAVE MCC MNC
- (void)saveMCCtoCache:(NSString*)mcc {
    
    if (userDefInfo) {
        [NSUserDefaults standardUserDefaults];
    }
    
    if (_traceMode) NSLog(@"Saving data - MCC...");
    if (mcc) {
        [userDefInfo setObject:mcc forKey:@"lastMCC"];
        if (_traceMode) NSLog(@"Data saved to persistent storage success. Stored data - MCC: \n%@", mcc);
    }
}

- (void)saveMNCtoCache:(NSString*)mnc {
    
    if (userDefInfo) {
        [NSUserDefaults standardUserDefaults];
    }
    
    if (_traceMode) NSLog(@"Saving data - MNC...");
    if (mnc) {
        [userDefInfo setObject:mnc forKey:@"lastMNC"];
        if (_traceMode) NSLog(@"Data saved to persistent storage success. Stored data - MNC: \n%@", mnc);
    }
}


//CLEAR MCC MNC
- (BOOL)clearMobileNetwork_MCC_MNC {
    
    if (userDefInfo) {
        [NSUserDefaults standardUserDefaults];
    }
    
    //Remove object
    [userDefInfo removeObjectForKey:@"lastMCC"];
    [userDefInfo removeObjectForKey:@"lastMNC"];
    
    //Still the objects stored?
    if ([self getMCC] || [self getMNC]) {
        return NO;//Fail removing
    }else{
        return YES;//Sucess removing
    }
}



//  URL ENCODER UTIL
+ (NSString *) urlEncode:(NSString *)source
{
    NSString *response=@"";
    if (source!=nil) {
        response=(NSString *)CFBridgingRelease(CFURLCreateStringByAddingPercentEscapes(
                                                                                       NULL, (CFStringRef)source, NULL,
                                                                                       (CFStringRef)@"!*'\"();:@&=+-~$,./?%#[]<>|\\{} ",
                                                                                       CFStringConvertNSStringEncodingToEncoding(NSUTF8StringEncoding)));
    }
    return response;
}


+ (NSString*)nameForSize:(logoSizeTypeEnum)size {
    
    switch (size) {
        case logoSizeTypeEnumSMALL:
            return @"small";
            break;
        case logoSizeTypeEnumMEDIUM:
            return @"medium";
            break;
        case logoSizeTypeEnumLARGE:
            return @"large";
            break;
        default:
            return nil;
    };
    return nil;
}

//+ (NSString*)nameForApi:(apiTypeEnum)api {
//    
//    switch (api) {
//        case apiTypeEnumPAYMENT:
//            return @"payment";
//            break;
//        case apiTypeEnumOPERATORID:
//            return @"operatorId";
//            break;
//        default:
//            return nil;
//            
//    };
//    return nil;
//}

+ (NSString*)nameForBg_color:(bg_colorTypeEnum)bg_color {
    
    switch (bg_color) {
        case bg_colorTypeEnumBLACK:
            return @"black";
            break;
        case bg_colorTypeEnumREVERSED:
            return @"reversed";
            break;
        case bg_colorTypeEnumNORMAL:
            return @"normal";
            break;
        default:
            return nil;
    };
    return nil;
}

+ (NSString*)nameForAspectRatio:(aspect_ratioTypeEnum)aspectRatio {
    
    switch (aspectRatio) {
        case aspect_ratioTypeEnumLANDSCAPE:
            return @"landscape";
            break;
        case aspect_ratioTypeEnumSQUARE:
            return @"square";
            break;
        default:
            return nil;
    };
    return nil;
}


@end
