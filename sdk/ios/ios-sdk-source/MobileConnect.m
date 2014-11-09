//
//  OpenID.m
//  GSMAIOSSDK
//
//  Created by sipbox on 01/07/14.
//  Copyright (c) 2014 Jaco. All rights reserved.
//

#import "MobileConnect.h"
#import "AuthorizationOptions.h"



@implementation MobileConnect

//Delegates for responses
@synthesize refreshTokenDelegate;
@synthesize revokeTokenDelegate;
@synthesize revokingAccessTokenDelegate;
@synthesize tokenFromAuthorizationCodeDelegate;
@synthesize userInfoDelegate;
@synthesize authorizationDelegate;

//Webview for open functions views

- (NSString*)nameForPrompt:(promptEnumType)prompt {
    
    switch (prompt) {
        case NONE:
            return @"none";
            break;
        case LOGIN:
            return @"login";
            break;
        case CONSENT:
            return @"consent";
            break;
        case SELECT_ACCOUNT:
            return @"select_account";
            break;
        default:
            return nil;
    };
    return nil;
}


-(void)authorize:(NSString *)url clientID:(NSString *)clientID  clientSecret:(NSString *)clientSecret  scope:(NSString *)scope redirectUri:(NSString *)redirectUri responseType:(NSString *)responseType state:(NSString *)state nonce:(NSString *)nonce prompt:(promptEnumType)prompt maxAge:(int)maxAge acrValues:(NSString *)acrValues authorizationOptions:(AuthorizationOptions *)authorizationOptions webview:(UIWebView *) webview {

    NSString* requestUrl = url;
    
    self.redirectUri=redirectUri;
    
    if (clientID){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&client_id=%@", [Utils urlEncode:clientID] ]];
    }
    if (clientID){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&client_secret=%@", [Utils urlEncode:clientSecret] ]];
    }
    if (scope){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&scope=%@", [Utils urlEncode:scope] ]];
    }
    if (redirectUri){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&redirect_uri=%@", [Utils urlEncode:redirectUri] ]];
    }
    if (responseType){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&response_type=%@", [Utils urlEncode:responseType] ]];
    }
    if (state){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&state=%@", [Utils urlEncode:state] ]];
    }
    if (nonce){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&nonce=%@", [Utils urlEncode:nonce]]];
    }
    if (prompt){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&prompt=%@", [Utils urlEncode:[self nameForPrompt:prompt]] ]];
    }
    if (maxAge>=0){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&max_age=%i", maxAge]];
    }
    if (acrValues){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&acr_values=%@", [Utils urlEncode:acrValues]]];
    }
    
    if (authorizationOptions){
        if (authorizationOptions->display){
            requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&display=%@", [Utils urlEncode:[AuthorizationOptions nameForDisplay: (    authorizationOptions->display)]]  ]];
        }
        if (authorizationOptions->ui_locales){
            requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&ui_locales=%@", [Utils urlEncode:authorizationOptions->ui_locales]  ]];
        }
        if (authorizationOptions->claims_locales){
            requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&claims_locales=%@", [Utils urlEncode:authorizationOptions->claims_locales]  ]];
        }
        if (authorizationOptions->id_token_hint){
            requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&id_token_hint=%@", [Utils urlEncode:authorizationOptions->id_token_hint] ]];
        }
        if (authorizationOptions->login_hint){
            requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&login_hint=%@", [Utils urlEncode:authorizationOptions->login_hint] ]];
        }
        if (authorizationOptions->dtbs){
            requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&dtbs=%@", [Utils urlEncode: authorizationOptions->dtbs] ]];
        }
    }
    
    
    //Replace first occurrence of '&' by a '?'
    NSRange firstRange = [requestUrl rangeOfString:@"&"];
    requestUrl = [requestUrl stringByReplacingOccurrencesOfString:@"&" withString:@"?" options:0 range:firstRange];
    
    if (_traceMode) NSLog(@"Url of the request to send: %@", requestUrl);
    
    if (webview) {
        if (!webview.delegate) webview.delegate=self;
        if (webview.hidden) webview.hidden=FALSE;
        
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:requestUrl]];
        
        NSString *uaString = [webview stringByEvaluatingJavaScriptFromString:@"navigator.userAgent"];
        if (_traceMode) NSLog(@"Setting user agent to %@", uaString);
        [request setValue:uaString forHTTPHeaderField:@"User-agent"];
        
        if (_traceMode) NSLog(@"Opening webview");
        [webview loadRequest:request];
    } else {
        if (_traceMode) NSLog(@"Opening browser");
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString: requestUrl]];
    }
    
}

-(void)refreshToken:(NSString *)url refreshToken:(NSString*)refreshToken  scope:(NSString*)scope clientID:(NSString*)clientID clientSecret:(NSString *)clientSecret {
 
    
    NSString* requestUrl = url;
    
    requestUrl = [requestUrl stringByAppendingString: @"?grant_type=refresh_token"];
    if (refreshToken){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&refresh_token=%@", [Utils urlEncode: refreshToken] ]];
    }
    if (scope){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&scope=%@", [Utils urlEncode: scope] ]];
    }

    if (_traceMode) NSLog(@"Url of the request to send: %@", requestUrl);
    
    // - - HEADERS - -
    
    // - - REQUEST CREATION - -
    NSMutableURLRequest *request;
    request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestUrl] ];
    // Request Type
    [request setHTTPMethod: @"POST"];
    
    //Accept header
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    //Content-type
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-type"];
    
    if (clientID) {
        // Set content-type header
        NSString* encodedString = [Utils getEncryption:encryptionTypeEnumBASIC client:clientID key:clientSecret traceMode:_traceMode];
        if (_traceMode) NSLog(@"Basic authentication %@", encodedString);
        [request setValue: [ NSString stringWithFormat: @"Basic %@",encodedString ] forHTTPHeaderField:@"Authorization"];
    }
    
    
    // - - REQUEST - -
    
    // Now send a request and get response Asynchronously
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    [NSURLConnection sendAsynchronousRequest:request queue:queue completionHandler:^(NSURLResponse *response, NSData *data, NSError *error){
        
        //Response object
        NSHTTPURLResponse* urlResponse = (NSHTTPURLResponse*)response;
        
        //Check request process result
        if (error){//Error case in the request process. NOT ERROR STATUS CODE --> CODE ERROR, TCP EXCHANGE ERROR, etc...
            
            if (data==nil) {
                [[self refreshTokenDelegate] refreshTokenError:(long)[urlResponse statusCode] data:nil];
            } else {
                //Trying to parse json response
                NSError* parseError;
                NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
                
                //Error parting JSON?
                if( parseError ) {
                    
                    if (_traceMode) NSLog(@"Error parsing JSON response: %@", [parseError localizedDescription] );
                    
                    if ([urlResponse  statusCode] == 0 ) {//Error parsing server response. Unknown format response
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. Not JSON in response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self refreshTokenDelegate] refreshTokenError:-1 data:nil ];
                        
                    }else{
                        if (_traceMode) NSLog(@"Error parsing server response. \n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self refreshTokenDelegate] refreshTokenError:(long)[urlResponse statusCode] data:nil];
                        
                    }
                
                }else{//Not error parsing JSON in error response from server
                    
                    if ([urlResponse  statusCode] == 0 ) {
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. JSON Response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self refreshTokenDelegate] refreshTokenError:-1 data:jsonResponseObj ];
                        
                    }else{
                        if (_traceMode) NSLog(@"Error response from server. Status code: %ld\nJSON Response:\n%@",(long)[urlResponse  statusCode],[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self refreshTokenDelegate] refreshTokenSuccessful:(long)[urlResponse statusCode] data:jsonResponseObj];
                    }
                }
            }

            
            
        }else{//Not error in request process
            
            // Parsing JSON to NSMutableDictionary object
            NSError* parseError;
            NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
            
            if( parseError ) {//Error parting JSON
                
                if ([urlResponse  statusCode] == 0 ) {
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    
                    [[self refreshTokenDelegate] refreshTokenError:-1 data:nil];
                }else{
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    
                    [[self refreshTokenDelegate] refreshTokenError:(long)[urlResponse statusCode] data:nil];
                }
                
            }else {
                
                if ([urlResponse  statusCode] == 0 ) {//JSON response body, but not status code provided/parsed
                    
                    if (_traceMode) NSLog( @"%@", [parseError localizedDescription] );
                    
                    [[self refreshTokenDelegate] refreshTokenError:-1 data:jsonResponseObj];

                }else{//JSON response body and status code provided
                    
                    [[self refreshTokenDelegate] refreshTokenSuccessful:(long)[urlResponse statusCode] data:jsonResponseObj];
                    
                }
            }
        }
    }];

}


-(void)revokeToken:(NSString*)url clientID:(NSString*)clientID clientSecret:(NSString *)clientSecret {

    
    NSString* requestUrl = url;
    
    requestUrl = [requestUrl stringByAppendingString: @"?scope=revoke"];
    
    if (_traceMode) NSLog(@"Url of the request to send: %@", requestUrl);
    
    
    // - - HEADERS - -
    
    // - - REQUEST CREATION - -
    NSMutableURLRequest *request;
    request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestUrl] ];
    // Request Type
    [request setHTTPMethod: @"POST"];
    
    //Accept header
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    //Content-type
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-type"];
    
    
    if (clientID) {
        // Set content-type header
        NSString* encodedString = [Utils getEncryption:encryptionTypeEnumBASIC client:clientID key:clientSecret traceMode:_traceMode];
        //NSLog(@"Basic authentication");
        [request setValue: [ NSString stringWithFormat: @"Basic %@",encodedString ] forHTTPHeaderField:@"Authorization"];
    }
    
    
    // - - REQUEST - -
    
    // Now send a request and get response Asynchronously
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    [NSURLConnection sendAsynchronousRequest:request queue:queue completionHandler:^(NSURLResponse *response, NSData *data, NSError *error){
        
        //Response object
        NSHTTPURLResponse* urlResponse = (NSHTTPURLResponse*)response;
        
        //Check request process result
        if (error){//Error case in the request process. NOT ERROR STATUS CODE --> CODE ERROR, TCP EXCHANGE ERROR, etc...
            
            if (data==nil) {
                [[self revokeTokenDelegate] revokeTokenError:(long)[urlResponse statusCode] data:nil];
            } else {
            
            //Trying to parse json response
            NSError* parseError;
            NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
                //Error parting JSON?
                if( parseError ) {
                    
                    if (_traceMode) NSLog(@"Error parsing JSON response: %@", [parseError localizedDescription] );
                    
                    if ([urlResponse  statusCode] == 0 ) {//Error parsing server response. Unknown format response
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. Not JSON in response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self revokeTokenDelegate] revokeTokenError:-1 data:nil];
                        
                    }else{
                        if (_traceMode) NSLog(@"Error parsing server response. \n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self revokeTokenDelegate] revokeTokenError:(long)[urlResponse statusCode] data:nil];
                        
                    }
                    
                }else{//Not error parsing JSON in error response from server
                    
                    if ([urlResponse  statusCode] == 0 ) {
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. JSON Response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self revokeTokenDelegate] revokeTokenError:-1 data:jsonResponseObj ];
                        
                    }else{
                        if (_traceMode) NSLog(@"Error response from server. Status code: %ld\nJSON Response:\n%@",(long)[urlResponse  statusCode],[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self revokeTokenDelegate] revokeTokenSuccessful:(long)[urlResponse statusCode] data:jsonResponseObj];
                    }
                }
            }
            
            
        }else{//Not error in request process
            
            // Parsing JSON to NSMutableDictionary object
            NSError* parseError;
            NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
            
            if( parseError ) {//Error parting JSON
                
                if ([urlResponse  statusCode] == 0 ) {
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    
                    [[self revokeTokenDelegate] revokeTokenError:-1 data:nil];
                }else{
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    
                    [[self revokeTokenDelegate] revokeTokenError:(long)[urlResponse statusCode] data:nil];
                }
                
            }else {
                
                if ([urlResponse  statusCode] == 0 ) {//JSON response body, but not status code provided/parsed
                    
                    if (_traceMode) NSLog( @"%@", [parseError localizedDescription] );
                    
                    [[self revokeTokenDelegate] revokeTokenError:-1 data:jsonResponseObj];
                    
                }else{//JSON response body and status code provided
                    
                    [[self revokeTokenDelegate] revokeTokenSuccessful:(long)[urlResponse statusCode] data:jsonResponseObj];
                    
                }
            }
        }
    }];
}


-(void)revokingAccessToken:(NSString*)url accessToken:(NSString*)accessToken clientID:(NSString*)clientID clientSecret:(NSString *)clientSecret{

    NSString* requestUrl = url;
    
    requestUrl = [requestUrl stringByAppendingString: @"?grant_type=access_token"];
    requestUrl = [requestUrl stringByAppendingString: @"&scope=revoke"];
    if (accessToken){
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&access_token=%@", [Utils urlEncode: accessToken] ]];
    }
    
    if (_traceMode) NSLog(@"Url of the request to send: %@", requestUrl);
    
    
    // - - HEADERS - -
    
    // - - REQUEST CREATION - -
    NSMutableURLRequest *request;
    request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestUrl] ];
    // Request Type
    [request setHTTPMethod: @"POST"];
    
    //Accept header
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    //Content-type
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-type"];

    if (clientID) {
        // Set content-type header
        NSString* encodedString = [Utils getEncryption:encryptionTypeEnumBASIC client:clientID key:clientSecret traceMode:_traceMode];
        //NSLog(@"Basic authentication");
        [request setValue: [ NSString stringWithFormat: @"Basic %@",encodedString ] forHTTPHeaderField:@"Authorization"];
    }
    
    
    // - - REQUEST - -
    
    // Now send a request and get response Asynchronously
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    [NSURLConnection sendAsynchronousRequest:request queue:queue completionHandler:^(NSURLResponse *response, NSData *data, NSError *error){
        
        //Response object
        NSHTTPURLResponse* urlResponse = (NSHTTPURLResponse*)response;
        
        //Check request process result
        if (error){//Error case in the request process. NOT ERROR STATUS CODE --> CODE ERROR, TCP EXCHANGE ERROR, etc...
            
            if (data==nil) {
                [[self revokingAccessTokenDelegate] revokingAccessTokenError:(long)[urlResponse statusCode] data:nil];
            } else {
                //Trying to parse json response
                NSError* parseError;
                NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
                
                //Error parting JSON?
                if( parseError ) {
                    
                    if (_traceMode) NSLog(@"Error parsing JSON response: %@", [parseError localizedDescription] );
                    
                    if ([urlResponse  statusCode] == 0 ) {//Error parsing server response. Unknown format response
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. Not JSON in response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self revokingAccessTokenDelegate] revokingAccessTokenError:-1 data:nil];
                        
                    }else{
                        if (_traceMode) NSLog(@"Error parsing server response. \n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self revokingAccessTokenDelegate] revokingAccessTokenError:(long)[urlResponse statusCode] data:nil];
                        
                    }
                    
                }else{//Not error parsing JSON in error response from server
                    
                    if ([urlResponse  statusCode] == 0 ) {
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. JSON Response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self revokingAccessTokenDelegate] revokingAccessTokenError:-1 data:jsonResponseObj ];
                        
                    }else{
                        if (_traceMode) NSLog(@"Error response from server. Status code: %ld\nJSON Response:\n%@",(long)[urlResponse  statusCode],[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self revokingAccessTokenDelegate] revokingAccessTokenSuccessful:(long)[urlResponse statusCode] data:jsonResponseObj];
                    }
                }
            }
            
            
        }else{//Not error in request process
            
            // Parsing JSON to NSMutableDictionary object
            NSError* parseError;
            NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
            
            if( parseError ) {//Error parting JSON
                
                if ([urlResponse  statusCode] == 0 ) {
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    
                    [[self revokingAccessTokenDelegate] revokingAccessTokenError:-1 data:nil];
                }else{
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    
                    [[self revokingAccessTokenDelegate] revokingAccessTokenError:(long)[urlResponse statusCode] data:nil];
                }
                
            }else {
                
                if ([urlResponse  statusCode] == 0 ) {//JSON response body, but not status code provided/parsed
                    
                    if (_traceMode) NSLog( @"%@", [parseError localizedDescription] );
                    
                    [[self revokingAccessTokenDelegate] revokingAccessTokenError:-1 data:jsonResponseObj];
                    
                }else{//JSON response body and status code provided
                    
                    [[self revokingAccessTokenDelegate] revokingAccessTokenSuccessful:(long)[urlResponse statusCode] data:jsonResponseObj];
                    
                }
            }
        }
    }];
    

}



- (void) tokenFromAuthorizationCode:(NSString*)url code:(NSString*)code clientID:(NSString*)clientID clientSecret:(NSString*)clientSecret redirectUri:(NSString*)redirectUri{

    NSString* requestUrl = url;
    
    NSString *requestData=@"grant_type=authorization_code";
    
    if (redirectUri){
        requestData = [requestData stringByAppendingString: [NSString stringWithFormat: @"&redirect_uri=%@", [Utils urlEncode: redirectUri] ]];
    }
    if (code){
        requestData = [requestData stringByAppendingString: [NSString stringWithFormat: @"&code=%@", [Utils urlEncode: code]]];
    }
    
    if (_traceMode) NSLog(@"Url of the request to send: %@", requestUrl);
    
    // - - HEADERS - -
    
    // - - REQUEST CREATION - -
    NSMutableURLRequest *request;
    request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestUrl] ];
    // Request Type
    [request setHTTPMethod: @"POST"];

    if (clientID) {
        // Set content-type header
        NSString* encodedString = [Utils getEncryption:encryptionTypeEnumBASIC client:clientID key:clientSecret traceMode:_traceMode];
        if (_traceMode) NSLog(@"Basic authentication %@", encodedString);
        [request setValue: [ NSString stringWithFormat: @"Basic %@",encodedString ] forHTTPHeaderField:@"Authorization"];
    }

    //Accept header
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    //Content-type
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-type"];
    
    NSData *requestBodyData = [requestData dataUsingEncoding:NSUTF8StringEncoding];
    request.HTTPBody = requestBodyData;

    // - - REQUEST - -
    
    // Now send a request and get response Asynchronously
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    [NSURLConnection sendAsynchronousRequest:request queue:queue completionHandler:^(NSURLResponse *response, NSData *data, NSError *error){
        
        //Response object
        NSHTTPURLResponse* urlResponse = (NSHTTPURLResponse*)response;
        
        //Check request process result
        if (error){//Error case in the request process. NOT ERROR STATUS CODE --> CODE ERROR, TCP EXCHANGE ERROR, etc...
            
            if (data==nil) {
                [[self tokenFromAuthorizationCodeDelegate] tokenFromAuthorizationCodeError:(long)[urlResponse statusCode] data:nil];
            } else {
                //Trying to parse json response
                NSError* parseError;
                NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
                
                //Error parting JSON?
                if( parseError ) {
                    
                    if (_traceMode) NSLog(@"Error parsing JSON response: %@", [parseError localizedDescription] );
                    
                    if ([urlResponse  statusCode] == 0 ) {//Error parsing server response. Unknown format response
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. Not JSON in response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self tokenFromAuthorizationCodeDelegate] tokenFromAuthorizationCodeError:-1 data:nil];
                        
                    }else{
                        if (_traceMode) NSLog(@"Error parsing server response. \n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self tokenFromAuthorizationCodeDelegate] tokenFromAuthorizationCodeError:(long)[urlResponse statusCode] data:nil];
                        
                    }
                    
                }else{//Not error parsing JSON in error response from server
                    
                    if ([urlResponse  statusCode] == 0 ) {
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. JSON Response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self tokenFromAuthorizationCodeDelegate] tokenFromAuthorizationCodeError:-1 data:jsonResponseObj ];
                        
                    }else{
                        if (_traceMode) NSLog(@"Error response from server. Status code: %ld\nJSON Response:\n%@",(long)[urlResponse  statusCode],[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        NSString *access_token=(NSString *)[jsonResponseObj objectForKey:@"access_token"];
                        NSString *_expires_in=(NSString *)[jsonResponseObj objectForKey:@"expires_in"];
                        NSNumber *expires_in=_expires_in!=nil?@([_expires_in intValue]):nil;
                        NSString *refresh_token=(NSString *)[jsonResponseObj objectForKey:@"refresh_token"];
                        
                        [[self tokenFromAuthorizationCodeDelegate] tokenFromAuthorizationCodeSuccessful:(long)[urlResponse statusCode] access_token:access_token expires_in:expires_in refresh_token:refresh_token data:jsonResponseObj];
                    }
                }
            }
            
        }else{//Not error in request process
            
            // Parsing JSON to NSMutableDictionary object
            NSError* parseError;
            NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
            
            if( parseError ) {//Error parting JSON
                
                if ([urlResponse  statusCode] == 0 ) {
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    
                    [[self tokenFromAuthorizationCodeDelegate] tokenFromAuthorizationCodeError:-1 data:nil];
                }else{
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    
                    [[self tokenFromAuthorizationCodeDelegate] tokenFromAuthorizationCodeError:(long)[urlResponse statusCode] data:nil];
                }
                
            }else {
                
                if ([urlResponse  statusCode] == 0 ) {//JSON response body, but not status code provided/parsed
                    
                    if (_traceMode) NSLog( @"%@", [parseError localizedDescription] );
                    
                    [[self tokenFromAuthorizationCodeDelegate] tokenFromAuthorizationCodeError:-1 data:jsonResponseObj];
                    
                }else{//JSON response body and status code provided

                    NSString *access_token=(NSString *)[jsonResponseObj objectForKey:@"access_token"];
                    NSString *_expires_in=(NSString *)[jsonResponseObj objectForKey:@"expires_in"];
                    NSNumber *expires_in=_expires_in!=nil?@([_expires_in intValue]):nil;
                    NSString *refresh_token=(NSString *)[jsonResponseObj objectForKey:@"refresh_token"];
                    
                    if (_traceMode) NSLog(@"Returning token to application");
                    
                    [[self tokenFromAuthorizationCodeDelegate] tokenFromAuthorizationCodeSuccessful:(long)[urlResponse statusCode] access_token:access_token expires_in:expires_in refresh_token:refresh_token data:jsonResponseObj];
                    
                }
            }
        }
    }];
}


- (void)userInfo:(NSString*)url accessToken:(NSString*)accessToken{
    NSString* requestUrl = url;
    
    // - - REQUEST CREATION - -
    NSMutableURLRequest *request;
    request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestUrl] ];
    
    // Request Type
    [request setHTTPMethod: @"GET"];

    //Authorization header
    if (accessToken) {
        NSString *authorization=[ NSString stringWithFormat: @"Bearer %@",accessToken ];
        if (_traceMode) NSLog(@"HTTP authentication %@", authorization);
        [request setValue:authorization forHTTPHeaderField:@"Authorization"];
    }

    //Accept header
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    //Content-type
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-type"];
    
    
    // - - REQUEST - -
    
    // Now send a request and get response Asynchronously
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    [NSURLConnection sendAsynchronousRequest:request queue:queue completionHandler:^(NSURLResponse *response, NSData *data, NSError *error){
        
        //Response object
        NSHTTPURLResponse* urlResponse = (NSHTTPURLResponse*)response;
        
        //Check request process result
        if (error){//Error case in the request process. NOT ERROR STATUS CODE --> CODE ERROR, TCP EXCHANGE ERROR, etc...
            
            if (data==nil) {
                 [[self userInfoDelegate] userInfoError:(long)[urlResponse statusCode] data:nil];
            } else {
            
                //Trying to parse json response
                NSError* parseError;
                NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
                
                //Error parting JSON?
                if( parseError ) {
                    
                    if (_traceMode) NSLog(@"Error parsing JSON response: %@", [parseError localizedDescription] );
                    
                    if ([urlResponse  statusCode] == 0 ) {//Error parsing server response. Unknown format response
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. Not JSON in response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self userInfoDelegate] userInfoError:-1 data:nil];
                        
                    }else{
                        if (_traceMode) NSLog(@"Error parsing server response. \n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self userInfoDelegate] userInfoError:(long)[urlResponse statusCode] data:nil];
                        
                    }
                    
                }else{//Not error parsing JSON in error response from server
                    
                    if ([urlResponse  statusCode] == 0 ) {
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. JSON Response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self userInfoDelegate] userInfoError:-1 data:jsonResponseObj ];
                        
                    }else{
                        if (_traceMode) NSLog(@"Error response from server. Status code: %ld\nJSON Response:\n%@",(long)[urlResponse  statusCode],[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        
                        [[self userInfoDelegate] userInfoSuccessful:(long)[urlResponse statusCode] data:jsonResponseObj];
                    }
                }
            }
            
        }else{//Not error in request process
            
            // Parsing JSON to NSMutableDictionary object
            NSError* parseError;
            NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
            
            if( parseError ) {//Error parting JSON
                
                if ([urlResponse  statusCode] == 0 ) {
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    
                    [[self userInfoDelegate] userInfoError:-1 data:nil];
                }else{
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    
                    [[self userInfoDelegate] userInfoError:(long)[urlResponse statusCode] data:nil];
                }
                
            }else {
                
                if ([urlResponse  statusCode] == 0 ) {//JSON response body, but not status code provided/parsed
                    
                    if (_traceMode) NSLog( @"%@", [parseError localizedDescription] );
                    
                    [[self userInfoDelegate] userInfoError:-1 data:jsonResponseObj];
                    
                }else{//JSON response body and status code provided
                    
                    [[self userInfoDelegate] userInfoSuccessful:(long)[urlResponse statusCode] data:jsonResponseObj];
                    
                }
            }
        }
    }];


}

-(BOOL)isAccessTokenValid:(NSTimeInterval*)timestamp{
    NSTimeInterval interval = [[NSDate date] timeIntervalSince1970];
    //NSTimeInterval interval2 = [[NSDate date] timeIntervalSince1970];
    
    //NSLog(@" Current time: %f",interval);
    //NSLog(@" Provided time: %f",interval2);
    
    if ( &interval < timestamp ) {
        return YES;
    }
    return NO;
}

- (NSMutableDictionary*)extractRedirectParameter:(NSString*)url{

    //NSString* url =  @"https://etelco-prod.apigee.net/etel/openId/index?sessionid=32c7dda1-cd9e-4e9c-faef-80e48f7ea665&mcc_mnc=1234_1234&code=blabla";
    
    @try {
        
        //Get parameters from url
        NSArray *params = [url componentsSeparatedByString:@"?"];
        url = [params objectAtIndex:1];
        if (_traceMode) NSLog(@"%@",url);
        
        // Create an empty mutable dictionary for the result
        NSMutableDictionary *jobs = [NSMutableDictionary dictionary];
        
        //Split parameters and generate the result
        NSArray *splitResult = [url componentsSeparatedByString:@"&"];
        for(int i=0;i<[splitResult count];i++){
            
            NSString *str=[splitResult objectAtIndex:i];
            NSArray *aa=[str componentsSeparatedByString:@"="];
            if (_traceMode) NSLog(@"%@  --> %@",[aa objectAtIndex:0], [aa objectAtIndex:1]);
            
            [jobs setObject: [aa objectAtIndex:1] forKey:[aa objectAtIndex:0]];
        }
        
        //NSLog(@"NSMutableDictionary \n%@", jobs);
        return jobs;
    }
    @catch (NSException *exception) {
        if (_traceMode) NSLog(@"Exception ocurred parsing the url.");
        
    }
    return nil;
}


//Loaing webview
- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType{
    if (_traceMode) NSLog(@"Loading URL :%@",request.URL.absoluteString);
    BOOL load=YES;
    
    if ([request.URL.absoluteString hasPrefix:self.redirectUri]) {
        
        MobileConnect *mobileConnectObj = [[MobileConnect alloc] init];
        NSMutableDictionary *parameters=[mobileConnectObj extractRedirectParameter:request.URL.absoluteString];
        if (parameters.count>0) {
            NSString *error=nil;
            NSString *state=nil;
            NSString *code=nil;
            NSString *error_description=nil;
            
            for (id key in parameters) {
                id value=[parameters objectForKey:key];
                if ([(NSString *)key isEqualToString:@"code"]) {
                    code=(NSString *) value;
                } else if ([(NSString *)key isEqualToString:@"state"]) {
                    state=(NSString *) value;
                } else if ([(NSString *)key isEqualToString:@"error"]) {
                    error=(NSString *) value;
                } else if ([(NSString *)key isEqualToString:@"error_description"]) {
                    error_description=(NSString *) value;
                }
            }
            if (_traceMode) NSLog(@"error = %@", error);
            if (_traceMode) NSLog(@"code = %@", code);
            if (_traceMode) NSLog(@"state = %@", state);
            if (_traceMode) NSLog(@"error_description = %@", error_description);
            
            if (error) {
                [[self authorizationDelegate] authorizationError:error errorDescription:error_description state: state];
            } else {
                [[self authorizationDelegate] authorizationSuccessful:code state:state];
                
            }
        }
        
        [webView stopLoading];
        webView.hidden=TRUE;
        load=NO;
    }
    
    //return FALSE; //to stop loading
    return load;
}

@end
