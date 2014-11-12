//
//  AuthorizacionOptions.m
//  GSMA-IOS_SDK
//
//  Created by sipbox on 01/07/14.
//  Copyright (c) 2014 Jaco. All rights reserved.
//

#import "AuthorizationOptions.h"

@implementation AuthorizationOptions


+ (NSString*)nameForDisplay:(displayEnumType)prompt {
    
    switch (prompt) {
        case PAGE:
            return @"page";
            break;
        case POPUP:
            return @"popup";
            break;
        case TOUCH:
            return @"touch";
            break;
        case WAP:
            return @"wap";
            break;
        default:
            return nil;
    };
    return nil;
}


@end
