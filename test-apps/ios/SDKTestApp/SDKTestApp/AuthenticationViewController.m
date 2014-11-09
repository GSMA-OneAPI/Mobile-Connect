//
//  AuthenticationViewController.m
//  SDKTestApp
//
//  Copyright (c) 2014 GSMA. All rights reserved.
//

#import "AuthenticationViewController.h"

@interface AuthenticationViewController ()

@end

@implementation AuthenticationViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.authenticationLogData=[[NSMutableArray alloc] init];
    self.tokenLogData=[[NSMutableArray alloc] init];
    self.userinfoLogData=[[NSMutableArray alloc] init];
    
    self.statusLog.delegate=self;
    self.statusLog.dataSource=self;

    NSLog(@"handling authentication process");
    
    self.mobileConnectProvider=[[MobileConnect alloc] init];
    self.mobileConnectProvider.traceMode=TRUE;
    self.mobileConnectProvider.authorizationDelegate=self;
    self.mobileConnectProvider.tokenFromAuthorizationCodeDelegate=self;
    self.mobileConnectProvider.userInfoDelegate=self;
    
    self.returnUri=@"http://oauth2callback.gsma.com/oauth2callback";
    
    NSLog(@"handle login request");
    
    self.discoveryProvider=[[DiscoveryAPI alloc] init];
    NSMutableDictionary *discoveryData=self.discoveryProvider.getCachedDiscoveryItem;
    
    NSString *client_id=[self.discoveryProvider responseField:discoveryData field:@"client_id"];
    NSString *authorizationurl=[self.discoveryProvider endpoint:discoveryData api:@"operatorid" function:@"authorization"];
    
    self.state = [[NSUUID UUID] UUIDString];
    NSString *nonce = [[NSUUID UUID] UUIDString];
    NSString *acrValues=nil;
    
    AuthorizationOptions *authorizationOptions=[[AuthorizationOptions alloc] init];
    authorizationOptions->ui_locales=@"en";
    authorizationOptions->display=PAGE;
    authorizationOptions->claims_locales=@"en";
    authorizationOptions->id_token_hint=nil;
    authorizationOptions->login_hint=nil;
    authorizationOptions->dtbs=nil;
    
    self.loginWindow.hidden=YES;
    
    [self.loginWindow setFrame:self.view.frame];

    [self.mobileConnectProvider authorize:authorizationurl
                                 clientID:client_id
                             clientSecret:nil
                                    scope:@"openid profile email userinfo"
                              redirectUri:self.returnUri
                             responseType:@"code"
                                    state:self.state
                                    nonce:nonce
                                   prompt:LOGIN
                                   maxAge:3600
                                acrValues:acrValues
                     authorizationOptions:authorizationOptions
                                  webview:self.loginWindow];
    
}

void storeLogMessage (NSMutableArray *store, NSString *label, NSString *value) {
    NSMutableDictionary *labelValue=[[NSMutableDictionary alloc] initWithCapacity:3] ;
    [labelValue setValue:label forKey:@"label"];
    if (value) {
        [labelValue setValue:value forKey:@"value"];
    } else {
        [labelValue setValue:value forKey:@""];
    }
    [labelValue setValue:[NSDate date] forKey:@"date"];
    [store addObject:labelValue];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void) authorizationSuccessful:(NSString *) code state:(NSString *) state {
    
    storeLogMessage(self.authenticationLogData, @"Status", @"Successful");
    storeLogMessage(self.authenticationLogData, @"code", code);
    storeLogMessage(self.authenticationLogData, @"state", state);
    
    NSMutableDictionary *discoveryData=self.discoveryProvider.getCachedDiscoveryItem;
    NSString *client_id=[self.discoveryProvider responseField:discoveryData field:@"client_id"];
    NSString *client_secret=[self.discoveryProvider responseField:discoveryData field:@"client_secret"];
    NSString *tokenurl=[self.discoveryProvider endpoint:discoveryData api:@"operatorid" function:@"token"];

    storeLogMessage(self.tokenLogData, @"Started", @"Token request");
    
    [self.statusLog performSelectorOnMainThread:@selector(reloadData) withObject:nil waitUntilDone:YES];

    [self.mobileConnectProvider tokenFromAuthorizationCode:tokenurl code:code clientID:client_id clientSecret:client_secret redirectUri:self.returnUri];
}

- (void) authorizationError:(NSString *)error errorDescription:(NSString *)errorDescription state:(NSString *) state {
    storeLogMessage(self.authenticationLogData, @"Status", @"Error");
    storeLogMessage(self.authenticationLogData, @"error", error);
    storeLogMessage(self.authenticationLogData, @"description", errorDescription);
    storeLogMessage(self.authenticationLogData, @"state", state);
    [self.statusLog performSelectorOnMainThread:@selector(reloadData) withObject:nil waitUntilDone:YES];
}

- (void) tokenFromAuthorizationCodeSuccessful:(long)statusCode access_token:(NSString *)access_token expires_in:(NSNumber *)expires_in refresh_token:(NSString *)refresh_token data:(NSMutableDictionary*)data {

    storeLogMessage(self.tokenLogData, @"Status", @"Token received");
    storeLogMessage(self.tokenLogData, @"access_token", access_token);
    storeLogMessage(self.tokenLogData, @"expires_in", [expires_in stringValue]);
    storeLogMessage(self.tokenLogData, @"refresh_token", refresh_token);

    NSLog(@"handling tokenFromAuthorizationCodeSuccessful statusCode=%ld access_token=%@, expires_in=%@, refresh_token=%@, all data=%@", statusCode, access_token, expires_in, refresh_token, data);
    NSMutableDictionary *discoveryData=self.discoveryProvider.getCachedDiscoveryItem;
    NSString *tokenurl=[self.discoveryProvider endpoint:discoveryData api:@"operatorid" function:@"userinfo"];
    if (tokenurl && access_token) {
        NSLog(@"dispatching userinfo request");
        storeLogMessage(self.userinfoLogData, @"Started", @"Userinfo request");
        [self.statusLog performSelectorOnMainThread:@selector(reloadData) withObject:nil waitUntilDone:YES];
        [self.mobileConnectProvider userInfo:tokenurl accessToken:access_token];
    } else {
        [self.statusLog performSelectorOnMainThread:@selector(reloadData) withObject:nil waitUntilDone:YES];
    }
}

- (void) tokenFromAuthorizationCodeError:(long)statusCode data:(NSMutableDictionary*)errorData {
    NSLog(@"handling tokenFromAuthorizationCodeError statusCode=%ld errorData=%@", statusCode, errorData);
    storeLogMessage(self.tokenLogData, @"Status", @"Error");
    storeLogMessage(self.tokenLogData, @"statusCode", [[NSString alloc] initWithFormat:@"%ld", statusCode]);
    [self.statusLog performSelectorOnMainThread:@selector(reloadData) withObject:nil waitUntilDone:YES];
}

- (void) userInfoSuccessful:(long)statusCode data:(NSMutableDictionary*)data {
    NSLog(@"handling userInfoSuccessful statusCode=%ld data=%@", statusCode, data);
    storeLogMessage(self.userinfoLogData, @"Status", @"Userinfo received");
    if (data && [data count]>0) {
        for(id key in data) {
            id value = [data objectForKey:key];
            if ([value isKindOfClass:[NSString class]]) {
                storeLogMessage(self.userinfoLogData, key, value);
            }
        }
    }
    [self.statusLog performSelectorOnMainThread:@selector(reloadData) withObject:nil waitUntilDone:YES];
}

- (void) userInfoError:(long)statusCode data:(NSMutableDictionary*)errorData {
    NSLog(@"handling userInfoError statusCode=%ld data=%@", statusCode, errorData);
    storeLogMessage(self.userinfoLogData, @"Status", @"Error");
    storeLogMessage(self.userinfoLogData, @"statusCode", [[NSString alloc] initWithFormat:@"%ld", statusCode]);
    [self.statusLog performSelectorOnMainThread:@selector(reloadData) withObject:nil waitUntilDone:YES];
}

- (NSInteger)numberOfSectionsInTableView: (UITableView *)tableView {
    return 3;
}

- (NSInteger)tableView: (UITableView *)tableView numberOfRowsInSection: (NSInteger)section {
    NSInteger result;
    switch (section) {
        case 0:
            result=[self.authenticationLogData count];
            break;
        case 1:
            result=[self.tokenLogData count];
            break;
        case 2:
            result=[self.userinfoLogData count];
            break;
        default:
            result=0;
            break;
    }
    return result;
}

- (UITableViewCell *)tableView: (UITableView *)tableView cellForRowAtIndexPath: (NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"LabelValueCellType" forIndexPath:indexPath];
    
    NSInteger section=indexPath.section;
    NSInteger row=indexPath.row;
    
    NSMutableArray *source=nil;
    switch (section) {
        case 0: source=self.authenticationLogData; break;
        case 1: source=self.tokenLogData; break;
        case 2: source=self.userinfoLogData; break;
    }
    NSMutableDictionary *labelValue=[source objectAtIndex:row];
    
    NSDateFormatter *timeFormat = [[NSDateFormatter alloc] init];
    [timeFormat setDateFormat:@"HH:mm:ss"];

    NSDate *itemdate=(NSDate *)[labelValue objectForKey:@"date"];
    
    NSString *labelText=(NSString *) [labelValue objectForKey:@"label"];
    NSString *valueText=(NSString *) [labelValue objectForKey:@"value"];
    NSString *dateText=[timeFormat stringFromDate:itemdate];
    
    UILabel *label = (UILabel *)[cell viewWithTag:1];
    [label setText:labelText];
    UILabel *value = (UILabel *)[cell viewWithTag:2];
    [value setText:valueText];
    UILabel *date = (UILabel *)[cell viewWithTag:3];
    [date setText:dateText];
    return cell;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    NSString *result;
    switch (section) {
        case 0:
            result=@"Authorisation";
            break;
        case 1:
            result=@"Token";
            break;
        case 2:
            result=@"Userinfo";
            break;
        default:
            result=@"unknown";
            break;
    }
    return result;
}

@end
