//
//  GSMA_iOS_SDK_Test_AppTests.m
//  GSMA iOS SDK Test AppTests
//
//  Created by Stephen Doyle on 21/10/2014.
//  Copyright (c) 2014 GSMA. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>

@interface GSMA_iOS_SDK_Test_AppTests : XCTestCase

@end

@implementation GSMA_iOS_SDK_Test_AppTests

- (void)setUp {
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)testExample {
    // This is an example of a functional test case.
    XCTAssert(YES, @"Pass");
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

@end
