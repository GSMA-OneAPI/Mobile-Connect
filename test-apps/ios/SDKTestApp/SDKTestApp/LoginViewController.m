//
//  LoginViewController.m
//  GSMA iOS SDK Test App
//
//  Copyright (c) 2014 GSMA. All rights reserved.
//

@import Foundation;

#import "LoginViewController.h"

@interface LoginViewController ()

@property (weak, nonatomic) IBOutlet UILabel *operatorNameValue;
@property (weak, nonatomic) IBOutlet UILabel *operatorCountryValue;
- (IBAction)loginWithMobileConnect:(id)sender;
@property (weak, nonatomic) IBOutlet UIButton *loginButton;

@property (nonatomic, strong) NSMutableData *receivedData;
@property (nonatomic, strong) NSURLConnection *imageConnection;
@end

@implementation LoginViewController


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    NSLog(@"In LoginViewController");
    NSLog(@"Retrieving discovery data");
    self.discoveryProvider=[[DiscoveryAPI alloc] init];
    NSMutableDictionary *discoveryData=self.discoveryProvider.getCachedDiscoveryItem;
    NSLog(@"Discovery data = %@", discoveryData);
    
    NSString *operator=[self.discoveryProvider responseField:discoveryData field:@"subscriber_operator"];
    NSString *country=[self.discoveryProvider responseField:discoveryData field:@"country"];
    NSString *currency=[self.discoveryProvider responseField:discoveryData field:@"currency"];
    NSString *client_id=[self.discoveryProvider responseField:discoveryData field:@"client_id"];
    NSString *client_secret=[self.discoveryProvider responseField:discoveryData field:@"client_secret"];

    NSLog(@"operator = %@", operator);
    NSLog(@"country = %@", country);
    NSLog(@"currency = %@", currency);
    NSLog(@"client_id = %@", client_id);
    NSLog(@"client_secret = %@", client_secret);
    
    self.operatorCountryValue.text=country;
    self.operatorNameValue.text=operator;
    
    NSString *authorization=[self.discoveryProvider endpoint:discoveryData api:@"operatorid" function:@"authorization"];
    NSString *token=[self.discoveryProvider endpoint:discoveryData api:@"operatorid" function:@"token"];
    NSString *userinfo=[self.discoveryProvider endpoint:discoveryData api:@"operatorid" function:@"userinfo"];

    NSLog(@"authorization = %@", authorization);
    NSLog(@"token = %@", token);
    NSLog(@"userinfo = %@", userinfo);
    
    LogoAPI *logoProvider=[[LogoAPI alloc] init];
    logoProvider.traceMode=TRUE;
    
    NSLog(@"Retrieving cached logo");
    
    UIImage *mobileConnectLogo=[logoProvider getCachedApiLogo:@"operatorid" size:logoSizeTypeEnumSMALL aspectRatio:aspect_ratioTypeEnumLANDSCAPE bg_color:bg_colorTypeEnumNORMAL operatorName:@"exchange" language:@"en"];
    
    NSLog(@"Read logo as %@", mobileConnectLogo);
    
    if (mobileConnectLogo!=nil) {
        dispatch_sync(dispatch_get_main_queue(), ^{
            [self.loginButton setBackgroundImage:mobileConnectLogo forState:UIControlStateNormal];
            [self.loginButton sizeToFit];
            [self.loginButton setTitle:@"" forState:UIControlStateNormal];
            
            [self.loginButton addTarget:self
                                 action:@selector(loginWithMobileConnect:)
                       forControlEvents:UIControlEventTouchUpInside];
        });
    }
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)loginWithMobileConnect:(id)sender {
    NSLog(@"go to authentication screen");
    [self performSegueWithIdentifier: @"displayAuthenticationScreen" sender: self];
}

-(IBAction) unwindToLoginScreen:(UIStoryboardSegue *)unwindSegue { }

@end
