//
//  LogoAPI.h
//  GSMAIOSSDK
//
//  Created by Hugo Galan on 30/06/14.
//  Copyright (c) 2014 Jaco. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Utils.h"
#import <UIKit/UIKit.h>


/** Formal protocol for logo API */
@protocol ProcessGetLogoResponseDelegate <NSObject>
@required

/**
 Callback function where the getLogo function will return success requested result.
 
 @param statusCode Response code for the getLogo request
 @param data Response data object for the getLogo request
 
 */
- (void) getLogoSuccessful:(long)statusCode data:(NSArray*)data;

/**
 Callback function where the getLogo function will return any error coming of the request.
 
 @param statusCode Result error status code returned by the getLogo request. '-1' value is returnet if parsing problems or not status code provided in the response
 @param errorData Response data object for the getLogo request
 
 */
- (void) getLogoError:(long)statusCode data:(NSMutableDictionary*)errorData;
@end


/**
 Class that provide functions for using the Logo API.
 */
@interface LogoAPI : NSObject{

    id <ProcessGetLogoResponseDelegate> logoDelegate;
    
    Utils* utils;
}

/** Property definition for trace mode (YES/NO)
 @property Logo delegate definition
 */
@property BOOL traceMode;

/** Property definition for Logo delegate
 @property Logo delegate definition
 */
@property  id logoDelegate;




//** Logo functions **/


/** Make the get logo request
 
 @param url Destination url location where the request will make
 @param logoSize Provided size of the logo. Types availables: 'SMALL','MEDIUM','LARGE'
 @param aspectRatio Provided aspect ratio. Types availables: 'SQUARE','LANDSCAPE'.
 @param bg_color Provided background color. Types availables: 'NORMAL','REVERSED','BLACK'
 @param ipAddress Provided ip address for the 'x-source-ip' header in the request
 */
- (void) getLogo:(NSString*)url logoSize:(logoSizeTypeEnum)logoSize aspectRatio:(aspect_ratioTypeEnum)aspectRatio bg_color:(bg_colorTypeEnum)bg_color ipAddress:(NSString*)ipAddress;

/** Make the get logo request
 
 @param url Destination url location where the request will make
 @param logoSize Provided size of the logo. Types availables: 'SMALL','MEDIUM','LARGE'
 @param aspectRatio Provided aspect ratio. Types availables: 'SQUARE','LANDSCAPE'.
 @param bg_color Provided background color. Types availables: 'NORMAL','REVERSED','BLACK'
 @param ipAddress Provided ip address for the 'x-source-ip' header in the request
 @param mcc Mobile Country Code provided
 @param mnc Mobile Network Code provided
 */
- (void) getLogo:(NSString*)url logoSize:(logoSizeTypeEnum)logoSize aspectRatio:(aspect_ratioTypeEnum)aspectRatio bg_color:(bg_colorTypeEnum)bg_color ipAddress:(NSString*)ipAddress mcc:(NSString*)mcc mnc:(NSString*)mnc;



/** This function return the last stored item representation of logo
 
 @return Array object representation of the result stored in the last success getLogo request made.
 */
- (NSArray*)getCachedLogoItem;


/** This function return the image url requested that fits the params provided. Null params will be ignored in the search
 
 @param api Conditional parameter to check in the logo stored item to return an specific logo url image. Types availables: 'PAYMENT','OPERATORID'
 @param size Conditional parameter to check in the logo stored item to return an specific logo url image. Types availables: 'SMALL','MEDIUM','LARGE'
 @param aspectRatio Conditional parameter to check in the logo stored item to return an specific logo url image. Types availables: 'SQUARE','LANDSCAPE'.
 @param bg_color Conditional parameter to check in the logo stored item to return an specific logo url image.Types availables: 'NORMAL','REVERSED','BLACK'
 @param operatorName Operator Conditional parameter to check in the logo stored item to return an specific logo url image
 @param language Conditional parameter for set the language if available
 
 @return Object representation of the result stored in the last success getLogo request made.
 */
- (UIImage*)getCachedApiLogo:(NSString*)api size:(logoSizeTypeEnum)size aspectRatio:(aspect_ratioTypeEnum)aspectRatio bg_color:(bg_colorTypeEnum)bg_color operatorName:(NSString*)operatorName language:(NSString *)language;

/**
 This function clear the logo item stored in the last successfull call to the getLogo function. This function clear too the last MCC MNC used and stored in application cache
 
 @return Boolean representation of the result
 */
- (BOOL)clearCachedLogoItem;


@end

