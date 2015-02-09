//
//  LogoAPI.m
//  GSMAIOSSDK
//
//  Created by Hugo Galan on 30/06/14.
//  Copyright (c) 2014 Jaco. All rights reserved.
//

#import "LogoAPI.h"
#import "Utils.h"


@implementation LogoAPI

@synthesize logoDelegate;



// - - GET LOGO FUNCTIONS - -

- (void) getLogo: (NSString*)url logoSize:(logoSizeTypeEnum)logoSize aspectRatio:(aspect_ratioTypeEnum)aspectRatio bg_color:(bg_colorTypeEnum)bg_color ipAddress:(NSString*)ipAddress{
    
    if (!utils) {
        utils = [[Utils alloc]init];
    }
    
    NSString* requestUrl = url;
    
    // - MCC MNC -
    NSString* mcc = [utils getMCC];
    NSString* mnc = [utils getMNC];
    
    if (mcc && mnc) {
        if (_traceMode) NSLog(@"Found mcc and mnc info in device.");
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&mcc_mnc=%@_%@", [Utils urlEncode:mcc], [Utils urlEncode:mnc]]];
        [utils saveMCCtoCache:mcc];
        [utils saveMNCtoCache:mnc];
    }else{
        if (_traceMode) NSLog(@"Not mcc or mnc info. Looking for it in cache...");
        //Not network info available. Cached data?
        mcc = [utils getCachedMCC];
        mnc = [utils getCachedMNC];
        
        if (mcc && mnc) {
            if (_traceMode) NSLog(@"Found mcc and mnc info in cache...");
            requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&mcc_mnc=%@_%@", [Utils urlEncode:mcc], [Utils urlEncode:mnc] ]];
        }else{
            // ERROR¿?¿? or send and wait to error response
            if (_traceMode) NSLog(@"ERROR obtaining the MCC MNC. Not param will be included in the request");
        }
    }
    // - PARAMS IN REQUEST URL -
    if (logoSize){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&logosize=%@", [Utils urlEncode: [Utils nameForSize:logoSize] ] ]];
    }
    if (aspectRatio){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&aspect_ratio=%@", [Utils urlEncode: [Utils nameForAspectRatio:aspectRatio] ] ]];
    }
    if (bg_color){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&bg_color=%@", [Utils urlEncode: [Utils nameForBg_color:bg_color] ] ]];
    }
    
    //Replace first occurrence of '&'
    NSRange firstRange = [requestUrl rangeOfString:@"&"];
    requestUrl = [requestUrl stringByReplacingOccurrencesOfString:@"&" withString:@"?" options:0 range:firstRange];
    
    if (_traceMode) NSLog(@"Url of the request to send: %@", requestUrl);
    
    // - - HEADERS - -
    
    // - - REQUEST CREATION - -
    NSMutableURLRequest *request;
    request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestUrl] ];
    // Request Type
    [request setHTTPMethod: @"GET"];
    
    
    
    //Accept header
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    //Content-type
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-type"];
    //IpAdresss
    if (ipAddress) {
        [request setValue:ipAddress forHTTPHeaderField:@"x-source-ip"];
    }
    
    // - - REQUEST - -
    
    // Now send a request and get response Asynchronously
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    [NSURLConnection sendAsynchronousRequest:request queue:queue completionHandler:^(NSURLResponse *response, NSData *data, NSError *error){
        
        //Response object
        NSHTTPURLResponse* urlResponse = (NSHTTPURLResponse*)response;
        
        //Check request process result
        if (error){//Error case in the request process.
            
            if (data==nil) {
                [[self logoDelegate] getLogoError:(long)[urlResponse  statusCode] data:nil];
            } else {
                
                //Trying to parse json response
                NSError* parseError;
                NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
                
                //Error parting JSON?
                if( parseError ) {
                    
                    if (_traceMode) NSLog(@"Error parsing JSON response: %@", [parseError localizedDescription] );
                    
                    if ([urlResponse  statusCode] == 0 ) {//Error parsing server response. Unknown format response
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. Not JSON in response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self logoDelegate] getLogoError:-1 data:nil ];
                    }else{
                        if (_traceMode) NSLog(@"Error parsing server response. \n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self logoDelegate] getLogoError:(long)[urlResponse  statusCode] data:nil];
                    }
                    
                }else{//Not error parsing JSON in error response from server
                    
                    if ([urlResponse  statusCode] == 0 ) {
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. JSON Response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self logoDelegate] getLogoError:-1 data:jsonResponseObj ];
                    }else{
                        if (_traceMode) NSLog(@"Error response from server. Status code: %ld\nJSON Response:\n%@",(long)[urlResponse  statusCode],[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self logoDelegate] getLogoError:(long)[urlResponse  statusCode] data:jsonResponseObj];
                    }
                }
            }
            
            
        }else{//Not error in request process
            
            // Parsing JSON to NSMutableDictionary object
            NSError* parseError;
            NSArray* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
            if( parseError ) {//Error parting JSON
                
                if ([urlResponse  statusCode] == 0 ) {
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    [[self logoDelegate] getLogoError:-1 data:nil ];
                }else{
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    [[self logoDelegate] getLogoError:(long)[urlResponse  statusCode] data:nil ];
                }
                
            }else {
                
                //Save dictionaryResponse to persistent mem
                [utils clearLastLogoResponse];
                [utils saveLastLogoResponse:jsonResponseObj];
                //NSLog(@"Logo response saved: %@", [self getLastLogoResponse]);
                
                if ([urlResponse  statusCode] == 0 ) {//JSON response body, but not status code provided/parsed
                    
                    if (_traceMode) NSLog( @"%@", [parseError localizedDescription] );
                    [[self logoDelegate] getLogoSuccessful:-1 data:jsonResponseObj];
                    
                }else{//JSON response body and status code provided
                    
                    [[self logoDelegate] getLogoSuccessful:(long)[urlResponse  statusCode] data:jsonResponseObj];
                }
            }
        }
    }];
}


- (void) getLogo:(NSString*)url logoSize:(logoSizeTypeEnum)logoSize aspectRatio:(aspect_ratioTypeEnum)aspectRatio bg_color:(bg_colorTypeEnum)bg_color ipAddress:(NSString*)ipAddress mcc:(NSString*)mcc mnc:(NSString*)mnc{

    if (!utils) {
        utils = [[Utils alloc]init];
    }
    
    NSString* requestUrl = url;
    
    // - MCC MNC -
    
    if (mcc && mnc) {
        if (_traceMode) NSLog(@"Found mcc and mnc info provided.");
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&mcc_mnc=%@_%@", [Utils urlEncode: mcc], [Utils urlEncode: mnc] ]];
        [utils saveMCCtoCache:mcc];
        [utils saveMNCtoCache:mnc];
    }else{
        if (_traceMode) NSLog(@"Not mcc or mnc info provided. Looking for it in cache...");
        //Not network info available. Cached data?
        mcc = [utils getCachedMCC];
        mnc = [utils getCachedMNC];
        
        if (mcc && mnc) {
            if (_traceMode) NSLog(@"Found mcc and mnc info in cache...");
            requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&mcc_mnc=%@_%@", [Utils urlEncode:mcc], [Utils urlEncode:mnc]]];
        }else{
            // ERROR¿?¿? or send and wait to error response
            if (_traceMode) NSLog(@"ERROR obtaining the MCC MNC. Not param will be included in the request");
        }
    }
    // - PARAMS IN REQUEST URL -
    if (logoSize){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&logosize=%@", [Utils urlEncode:[Utils nameForSize:logoSize]] ]];
    }
    if (aspectRatio){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&aspect_ratio=%@", [Utils urlEncode: [Utils nameForAspectRatio:aspectRatio] ] ]];
    }
    if (bg_color){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&bg_color=%@", [Utils urlEncode: [Utils nameForBg_color:bg_color]] ]];
    }
    
    //Replace first occurrence of '&'
    NSRange firstRange = [requestUrl rangeOfString:@"&"];
    requestUrl = [requestUrl stringByReplacingOccurrencesOfString:@"&" withString:@"?" options:0 range:firstRange];
    
    if (_traceMode) NSLog(@"Url of the request to send: %@", requestUrl);
    
    
    
    // - - HEADERS - -
    
    // - - REQUEST CREATION - -
    NSMutableURLRequest *request;
    request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestUrl] ];
    // Request Type
    [request setHTTPMethod: @"GET"];
    
    
    
    //Accept header
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    //Content-type
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-type"];
    //IpAdresss
    if (ipAddress) {
        [request setValue:ipAddress forHTTPHeaderField:@"x-source-ip"];
    }
    
    // - - REQUEST - -
    
    // Now send a request and get response Asynchronously
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    [NSURLConnection sendAsynchronousRequest:request queue:queue completionHandler:^(NSURLResponse *response, NSData *data, NSError *error){
        
        //Response object
        NSHTTPURLResponse* urlResponse = (NSHTTPURLResponse*)response;
        
        //Check request process result
        if (error){//Error case in the request process.
            
            if (data==nil) {
                [[self logoDelegate] getLogoError:(long)[urlResponse  statusCode] data:nil];
            } else {
                
                //Trying to parse json response
                NSError* parseError;
                NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
                
                //Error parting JSON?
                if( parseError ) {
                    
                    if (_traceMode) NSLog(@"Error parsing JSON response: %@", [parseError localizedDescription] );
                    
                    if ([urlResponse  statusCode] == 0 ) {//Error parsing server response. Unknown format response
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. Not JSON in response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self logoDelegate] getLogoError:-1 data:nil ];
                    }else{
                        if (_traceMode) NSLog(@"Error parsing server response. \n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self logoDelegate] getLogoError:(long)[urlResponse  statusCode] data:nil];
                    }
                    
                }else{//Not error parsing JSON in error response from server
                    
                    if ([urlResponse  statusCode] == 0 ) {
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. JSON Response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self logoDelegate] getLogoError:-1 data:jsonResponseObj ];
                    }else{
                        if (_traceMode) NSLog(@"Error response from server. Status code: %ld\nJSON Response:\n%@",(long)[urlResponse  statusCode],[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self logoDelegate] getLogoError:(long)[urlResponse  statusCode] data:jsonResponseObj];
                    }
                }
            }
            
        }else{//Not error in request process
            
            // Parsing JSON to NSMutableDictionary object
            NSError* parseError;
            NSArray* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
            if( parseError ) {//Error parting JSON
                
                if ([urlResponse  statusCode] == 0 ) {
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    [[self logoDelegate] getLogoError:-1 data:nil ];
                }else{
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    [[self logoDelegate] getLogoError:(long)[urlResponse  statusCode] data:nil ];
                }
                
            }else {
                
                //Save dictionaryResponse to persistent mem
                [utils clearLastLogoResponse];
                [utils saveLastLogoResponse:jsonResponseObj];
                //NSLog(@"Logo response saved: %@", [self getLastLogoResponse]);
                
                if ([urlResponse  statusCode] == 0 ) {//JSON response body, but not status code provided/parsed
                    
                    if (_traceMode) NSLog( @"%@", [parseError localizedDescription] );
                    [[self logoDelegate] getLogoSuccessful:-1 data:jsonResponseObj];
                    
                }else{//JSON response body and status code provided
                    
                    [[self logoDelegate] getLogoSuccessful:(long)[urlResponse  statusCode] data:jsonResponseObj];
                }
            }
        }
    }];
    
}

- (NSArray*)getCachedLogoItem{
    
    if (!utils) {
        utils = [[Utils alloc]init];
    }
    
    return [utils getLastLogoResponse];
    
}

- (UIImage*)getCachedApiLogo:(NSString*)api size:(logoSizeTypeEnum)size aspectRatio:(aspect_ratioTypeEnum)aspectRatio bg_color:(bg_colorTypeEnum)bg_color operatorName:(NSString*)operatorName language:(NSString *)language {
    
    if (!utils) {
        utils = [[Utils alloc]init];
    }
    
    NSArray* logoItem =[utils getLastLogoResponse];
    
    if (_traceMode) {
        NSLog(@"logo cache holds %@", logoItem);
        NSLog(@"searching for api = %@", api);
        NSLog(@"searching for size = %d (%@)", size, [Utils nameForSize:size]);
        NSLog(@"searching for aspectRatio = %d (%@)", aspectRatio, [Utils nameForAspectRatio:aspectRatio]);
        NSLog(@"searching for bg_color = %d (%@)", bg_color, [Utils nameForBg_color:bg_color]);
        NSLog(@"searching for operator = %@", operatorName);
        NSLog(@"searching for language = %@", language);
    }
    
    // values in foreach loop
    for (int i = 0; i < [logoItem count]; i++ ) {
        //NSLog(@"MY PARAM Values: api: %@, size: %@, aspectRatio: %@, bgColor: %@", [utils nameForApi:api], [utils nameForSize:size], [utils nameForAspectRatio:aspectRatio], [utils nameForBg_color:bg_color]);
        //NSLog(@"PARAMS IN MEM Values: apiName: %@, size: %@, aspectRatio: %@, bgColor: %@", [[logoItem objectAtIndex:i] valueForKey:@"apiName"], [[logoItem objectAtIndex:i] valueForKey:@"size"], [[logoItem objectAtIndex:i] valueForKey:@"aspectRatio"], [[logoItem objectAtIndex:i] valueForKey:@"bgColor"]);
        
        NSArray *current=[logoItem objectAtIndex:i];
        //NSLog(@"Checking logo %d", i);
        
        BOOL valid=TRUE;
        if (api && [[current valueForKey:@"apiName"] compare:api options:NSCaseInsensitiveSearch]!=NSOrderedSame) {
            valid=FALSE;
            //NSLog(@"mismatch on api %@ %@", api, [current valueForKey:@"apiName"]);
        }
        if (size && [[current valueForKey:@"size"] compare:[Utils nameForSize:size] options:NSCaseInsensitiveSearch]!=NSOrderedSame) {
            valid=FALSE;
            //NSLog(@"mismatch on size %d (%@) %@", size, [Utils nameForSize:size], [current valueForKey:@"size"]);
        }
        if (aspectRatio && [[current valueForKey:@"aspectRatio"] compare:[Utils nameForAspectRatio:aspectRatio] options:NSCaseInsensitiveSearch]!=NSOrderedSame) {
            valid=FALSE;
            //NSLog(@"mismatch on aspect ratio %d (%@) %@", aspectRatio, [Utils nameForAspectRatio:aspectRatio], [current valueForKey:@"aspectRatio"] );
        }
        if (bg_color && [[current valueForKey:@"bgColor"] compare:[Utils nameForBg_color:bg_color] options:NSCaseInsensitiveSearch]!=NSOrderedSame) {
            valid=FALSE;
            //NSLog(@"mismatch on bg color %d (%@) %@", bg_color, [Utils nameForBg_color:bg_color], [current valueForKey:@"bgColor"] );
        }
        if (operatorName && [[current valueForKey:@"operatorId"] compare:operatorName options:NSCaseInsensitiveSearch]!=NSOrderedSame) {
            valid=FALSE;
            //NSLog(@"mismatch on operatorName %@ %@", operatorName, [current valueForKey:@"operatorId"]);
        }
        if (language && [[current valueForKey:@"language"] compare:language options:NSCaseInsensitiveSearch]!=NSOrderedSame) {
            valid=FALSE;
            //NSLog(@"mismatch on language %@ %@", language, [current valueForKey:@"language"]);
        }
        
        if (valid) {
            //            NSLog(@"found details");
            
            NSString* logoUrl = [[logoItem objectAtIndex:i] valueForKey:@"url"] ;
            
            //Necesary code for working (fix some we
            NSString *url = [logoUrl stringByReplacingOccurrencesOfString: @":443/" withString:@"/"];
            if (_traceMode) NSLog(@"Logo url %@ changed to %@", logoUrl, url);
            NSURL *imageURL = [NSURL URLWithString:url];
            NSData *imageData = [NSData dataWithContentsOfURL:imageURL];
            UIImage *image = [UIImage imageWithData:imageData];
            
            if (_traceMode) {
                if (image) {
                    NSLog(@"Image loaded OK");
                } else {
                    NSLog(@"Image failed to load");
                }
            }
            
            return image;
            
        }
        
    }
    
    return nil;
}

- (BOOL)clearCachedLogoItem{
    
    if (!utils) {
        utils = [[Utils alloc]init];
    }
    
    [utils clearMobileNetwork_MCC_MNC];
    
    return [utils clearLastLogoResponse];
    
}

@end
