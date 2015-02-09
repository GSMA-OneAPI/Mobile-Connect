//
//  ViewController.m
//  GSMA iOS SDK Test App
//
//  Copyright (c) 2014 GSMA. All rights reserved.
//

#import "DiscoveryViewController.h"
#import <Foundation/Foundation.h>
#import <CoreTelephony/CoreTelephonyDefines.h>
#import <CoreTelephony/CTCarrier.h>
#import <CoreTelephony/CTTelephonyNetworkInfo.h>


@interface DiscoveryViewController ()
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *discoveryWorkingIndicator;

@end

@implementation DiscoveryViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
    self.operatorPicker.delegate = self;
    self.operatorPicker.dataSource = self;
    
    self.discoveryProvider=[[DiscoveryAPI alloc] init];
    
    self.discoveryProvider.traceMode=TRUE;
    self.discoveryProvider.discoveryDelegate=self;
    
    self.logoProvider=[[LogoAPI alloc] init];
    self.logoProvider.logoDelegate=self;
    self.logoProvider.traceMode=TRUE;
    
    [self.discoveryWorkingIndicator setHidesWhenStopped:YES];
    [self.discoveryWorkingIndicator stopAnimating];
    self.discoveryWorkingIndicator.hidden=TRUE;
    
    CTTelephonyNetworkInfo *netInfo = [[CTTelephonyNetworkInfo alloc] init];
    CTCarrier *carrier = [netInfo subscriberCellularProvider];
    NSString *mcc = [carrier mobileCountryCode];
    NSString *mnc = [carrier mobileNetworkCode];
    
    self.redirectUri=@"http://gsma.com/oneapi";
    
    self.discoveryurl=@"https://stage-exchange-test.apigee.net/gsma/v2/discovery";
    self.logourl=@"https://sb1.exchange.gsma.com/v1/logo";
    self.clientId=@"gZJ8mEnjoLiAgrfudHCEZvufOoafvf1S";
    self.clientSecret=@"oESO7jLriPaF3qKA";

    NSLog(@"Carrier name = %@",[carrier carrierName]);
    NSLog(@"MCC = %@",mcc);
    NSLog(@"MNC = %@",mnc);

    self.discoveryWebview=[[UIWebView alloc]initWithFrame:self.view.frame];
    [self.view addSubview:self.discoveryWebview];

    self.discoveryWebview.hidden=true;
    self.discoveryWebview.delegate=self.discoveryProvider;
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(IBAction) startButtonPressed:(id) buttonId {
    NSLog(@"start button pressed");
    
    NSString *mcc=[self selectedMcc];
    NSString *mnc=[self selectedMnc];
    
    NSLog(@"Setting up passive discovery request for %@", self.redirectUri);

    [self.discoveryWorkingIndicator startAnimating];
    self.discoveryWorkingIndicator.hidden=FALSE;

    [self.discoveryProvider getDiscoveryPassive:self.discoveryurl
                                       clientId:self.clientId
                                   clientSecret:self.clientSecret
                                     encryption:encryptionTypeEnumBASIC
                                      ipAddress:nil
                                    redirectUri:self.redirectUri
                                        msisdn:nil
                                           mcc:mcc
                                           mnc:mnc];
    
    [self.logoProvider getLogo:self.logourl logoSize:logoSizeTypeEnumSMALL aspectRatio:aspect_ratioTypeEnumLANDSCAPE bg_color:bg_colorTypeEnumNORMAL ipAddress:nil mcc:mcc mnc:mnc];
}

-(IBAction) startActiveButtonPressed:(id) buttonId {
    NSLog(@"start active button pressed");
    
    NSString *mcc=[self selectedMcc];
    NSString *mnc=[self selectedMnc];

    NSLog(@"Setting up active discovery request for %@", self.redirectUri);
    [self.discoveryWorkingIndicator startAnimating];
    self.discoveryWorkingIndicator.hidden=FALSE;
    
    NSLog(@"starting active discovery");
    [self.discoveryProvider getDiscoveryActive:self.discoveryurl
                                      clientId:self.clientId
                                  clientSecret:self.clientSecret
                                    encryption:encryptionTypeEnumBASIC
                                     ipAddress:nil
                                   redirectUri:self.redirectUri
                                        msisdn:nil
                                           mcc:mcc
                                           mnc:mnc
                                       webview:self.discoveryWebview];
    
    [self.logoProvider getLogo:self.logourl logoSize:logoSizeTypeEnumSMALL aspectRatio:aspect_ratioTypeEnumLANDSCAPE bg_color:bg_colorTypeEnumNORMAL ipAddress:nil mcc:mcc mnc:mnc];
}

-(IBAction) clearCacheButtonPressed:(id) buttonId {
    NSLog(@"clearing discovery and logo caches");
    [self.discoveryProvider clearCachedDiscoveryItem];
    [self.logoProvider clearCachedLogoItem];

    self.discoveryWorkingIndicator.hidden=TRUE;
    [self.discoveryWorkingIndicator stopAnimating];
}

- (void) getDiscoverySuccessful:(long)statusCode data:(NSMutableDictionary*)data {
    NSLog(@"discovery completed");
    NSLog(@"statusCode - %ld",statusCode);
    NSLog(@"result - %@",data);
    
    dispatch_sync(dispatch_get_main_queue(), ^{
        self.discoveryWorkingIndicator.hidden=TRUE;
        [self.discoveryWorkingIndicator stopAnimating];
    });
    [self performSegueWithIdentifier: @"displayLoginScreen" sender: self];
}

- (void) getDiscoveryError:(long)statusCode data:(NSMutableDictionary*)errorData {
    NSLog(@"discovery error");
    NSLog(@"statusCode - %ld",statusCode);
    NSLog(@"result - %@",errorData);
    dispatch_sync(dispatch_get_main_queue(), ^{
        self.discoveryWorkingIndicator.hidden=TRUE;
        [self.discoveryWorkingIndicator stopAnimating];
    });
}

- (void) getLogoSuccessful:(long)statusCode data:(NSMutableDictionary*)data {
    NSLog(@"logo successful");
    NSLog(@"statusCode - %ld",statusCode);
    NSLog(@"result - %@",data);
}

- (void) getLogoError:(long)statusCode data:(NSMutableDictionary*)errorData {
    NSLog(@"logo error");
    NSLog(@"statusCode - %ld",statusCode);
    NSLog(@"result - %@",errorData);
}


- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent: (NSInteger)component
{
    return 4;
}

-(NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row   forComponent:(NSInteger)component
{
    NSString *value=nil;
    switch (row) {
        case 0: value=@"None"; break;
        case 1: value=@"ETel (000-01)"; break;
        case 2: value=@"Dialog Sri Lanka (413-02)"; break;
        case 3: value=@"Mobitel Sri Lanka (413-01)"; break;
            
    }
    return value;
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row   inComponent:(NSInteger)component{
    NSString *mcc=[self selectedMcc];
    NSString *mnc=[self selectedMnc];

    NSLog(@"Selected Row %d, mcc=%@, mnc=%@", (int) row, mcc, mnc);
}

- (NSString *) selectedMcc {
    NSString *value=nil;
    NSInteger index=[self.operatorPicker selectedRowInComponent:0];
    switch (index) {
        case 0: value=@""; break;
        case 1: value=@"000"; break;
        case 2: value=@"413"; break;
        case 3: value=@"413"; break;
    }
    return value;
}

- (NSString *) selectedMnc {
    NSString *value=nil;
    NSInteger index=[self.operatorPicker selectedRowInComponent:0];
    switch (index) {
        case 0: value=@""; break;
        case 1: value=@"01"; break;
        case 2: value=@"02"; break;
        case 3: value=@"01"; break;
    }
    return value;
}

-(IBAction) unwindToDiscoveryScreen:(UIStoryboardSegue *)unwindSegue { }

@end
