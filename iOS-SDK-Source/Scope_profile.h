//
//  Scope_profile.h
//  GSMA-IOS_SDK
//
//  Created by sipbox on 02/07/14.
//  Copyright (c) 2014 sipbox. All rights reserved.
//

#import <Foundation/Foundation.h>



@interface Scope_profile : NSObject
{

@public

    NSString* name;
    NSString* family_name;
    NSString* given_name;
    NSString* middle_name;
    NSString* nickname;
    NSString* preferred_username;
    NSString* profile;
    NSString* picture;
    NSString* website;
    NSString* gender;
    NSString* birthdate;
    NSString* zoneinfo;
    NSString* locale;
    NSString* updated_at;

}

@end
