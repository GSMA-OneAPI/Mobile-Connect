//
//  LoginViewController.h
//  GSMA iOS SDK Test App
//
//  Copyright (c) 2014 GSMA. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DiscoveryAPI.h"
#import "LogoAPI.h"

@interface LoginViewController : UIViewController<UIWebViewDelegate>

@property (strong, nonatomic) DiscoveryAPI *discoveryProvider;

-(IBAction) unwindToLoginScreen:(UIStoryboardSegue *)unwindSegue;

@end
