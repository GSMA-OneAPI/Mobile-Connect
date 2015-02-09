//
//  AuthorizacionOptions.h
//  GSMA-IOS_SDK
//
//  Created by sipbox on 01/07/14.
//  Copyright (c) 2014 sipbox. All rights reserved.
//

#import <Foundation/Foundation.h>



typedef enum{
    PAGE = 1,
    POPUP,
    TOUCH,
    WAP
    
} displayEnumType;

/** Authorization optiones object
 */
@interface AuthorizationOptions : NSObject
{
    

    
@public
    
    /**
     ASCII String value to specify the user interface display for the Authentication and Consent flow.
     The values can be:
     page: Default value, if the display parameter is not added. The UI SHOULD be consistent with a full page view of the User-Agent.
     popup: The popup window SHOULD be 450px X 500px [wide X tall].
     touch: The Authorisation Server SHOULD display the UI consistent with a “touch” based interface.
     wap: The UI SHOULD be consistent with a “feature-phone” device display.
     */
    displayEnumType display;
    /**
     Space separated list of user preferred languages and scripts for the UI as per RFC5646. This parameter is for guidance only and in case the locales are not supported, error SHOULD NOT be returned.
     */
    NSString* ui_locales;
    /**
     Space separated list of user preferred languages and scripts for the Claims being returned as per RFC5646. This parameter is for guidance only and in case the locales are not supported, error SHOULD NOT be returned.
     */
    NSString* claims_locales;
    /**
     Generally used in conjunction with prompt=none to pass the previously issued ID Token as a hint for the current or past authentication session. If the ID Token is still valid and the user is logged in then the server returns a positive response, otherwise SHOULD return a login_error response. For the ID Token, the server need not be listed as audience, when included in the id_token_hint.
     */
    NSString* id_token_hint;
    /**
     An indication to the IDP/Authorisation Server on what ID to use for login, e.g. emailid, MSISDN (phone_number) etc. It is Recommended that the value matches the value used in Discovery.
     
     The login_hint can contain the MSISDN or the Encrypted MSISDN and SHOULD be tagged as MSISDN:<Value> and ENCR_MSISDN:<Value> respectively – in case MSISDN or Encrypted MSISDN is passed in login_hint.
     */
    NSString* login_hint;
    /**
     Data To Be signed. The Data/String to be signed by the private key owned by the end-user.
     The signed data is returned in the ID Claim, as private JWT claims for this profile.
     */
    NSString* dtbs;
    
}


+ (NSString*)nameForDisplay:(displayEnumType)prompt;


@end
