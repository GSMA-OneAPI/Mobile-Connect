//
//  AuthenticationViewController.h
//  SDKTestApp
//
//  Copyright (c) 2014 GSMA. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DiscoveryAPI.h"
#import "MobileConnect.h"
#import "AuthorizationOptions.h"

@interface AuthenticationViewController : UIViewController<ProcessAuthorizationDelegate,ProcessTokenFromAuthorizationCodeDelegate, ProcessUserInfoDelegate, UITableViewDelegate, UITableViewDataSource>

@property (strong, nonatomic) MobileConnect *mobileConnectProvider;
@property (strong, nonatomic) DiscoveryAPI *discoveryProvider;

@property (strong, nonatomic) NSString *returnUri;
@property (strong, nonatomic) NSString *state;
@property (strong, nonatomic) NSString *code;
@property (strong, nonatomic) NSString *access_token;
@property (strong, nonatomic) NSString *refresh_token;

@property (weak, nonatomic) IBOutlet UIWebView *loginWindow;
@property (weak, nonatomic) IBOutlet UITableView *statusLog;

@property (strong, nonatomic) NSMutableArray *authenticationLogData;
@property (strong, nonatomic) NSMutableArray *tokenLogData;
@property (strong, nonatomic) NSMutableArray *userinfoLogData;

@end
