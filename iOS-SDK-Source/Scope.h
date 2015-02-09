//
//  Scope.h
//  GSMA-IOS_SDK
//
//  Created by sipbox on 02/07/14.
//  Copyright (c) 2014 Jaco. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Scope_profile.h"

@interface Scope : NSObject
{
    

    @public

    Scope_profile* profile;
    NSString* email;
    NSString* address;
    NSString* phone;
    NSString* iffline_access;
    

}

@end
