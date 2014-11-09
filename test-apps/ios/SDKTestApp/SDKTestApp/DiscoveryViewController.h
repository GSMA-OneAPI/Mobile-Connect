//
//  DiscoveryViewController
//  GSMA iOS SDK Test App
//
//  Copyright (c) 2014 GSMA. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DiscoveryAPI.h"
#import "LogoAPI.h"


@interface DiscoveryViewController : UIViewController<ProcessGetDiscoveryResponseDelegate,ProcessGetLogoResponseDelegate,UIPickerViewDataSource,UIPickerViewDelegate,UIWebViewDelegate>

-(IBAction) startButtonPressed:(id) buttonId;
-(IBAction) startActiveButtonPressed:(id) buttonId;
-(IBAction) clearCacheButtonPressed:(id) buttonId;
-(IBAction) unwindToDiscoveryScreen:(UIStoryboardSegue *)unwindSegue;

@property (strong, nonatomic) IBOutlet      UIPickerView *operatorPicker;

- (NSString *) selectedMnc;
- (NSString *) selectedMcc;

@property (strong, nonatomic) DiscoveryAPI *discoveryProvider;

@property (strong, nonatomic) LogoAPI *logoProvider;

@property (strong, nonatomic) UIWebView *discoveryWebview;

@property (strong, nonatomic) NSString *redirectUri;
@property (strong, nonatomic) NSString *discoveryurl;
@property (strong, nonatomic) NSString *logourl;
@property (strong, nonatomic) NSString *clientId;
@property (strong, nonatomic) NSString *clientSecret;

@end

