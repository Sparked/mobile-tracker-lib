//
//  SparkedTracker.m
//
//  Created by Sparked
//  Please add this file to your app folder in your app workspace
//

#import "SparkedTracker.h"
#import "SPTracker.h"
#import "SPEmitter.h"
#import "SPSubject.h"


@implementation SparkedTracker {}

// create an SP tracker with emitter here
+ (SPTracker *) createTracker:(NSString *)appid_ url:(NSString *)url_ {
    SPEmitter *emitter = [SPEmitter build:^(id<SPEmitterBuilder> builder) {
        [builder setUrlEndpoint:url_];
        [builder setHttpMethod:SPRequestGet];
        [builder setProtocol:SPHttps];
        [builder setEmitRange:500];
        [builder setEmitThreadPoolSize:20];
        [builder setByteLimitPost:52000];
    }];
    
    SPTracker *tracker = [SPTracker build:^(id<SPTrackerBuilder> builder) {
        [builder setEmitter:emitter];
        [builder setAppId:appid_];
        [builder setBase64Encoded:false];
        [builder setSessionContext:YES];
    }];
    return tracker;
}

// update the user info in the tracker with the following data:
// accountId, userId, account startDate, account attributes, user attributes
+ (void) updateUser:(SPTracker *) tracker_
          accountId:(NSString *) accountId
        accountName:(NSString *) accountName
       accountEmail:(NSString *) accountEmail
   accountStartDate:(NSString *) accountStartDate
  accountAttributes:(NSDictionary *) accountAttributes
             userId:(NSString *) userId
           userName:(NSString *) userName
          userEmail:(NSString *) userEmail
     userAttributes:(NSDictionary *) userAttributes {
    
    // making userData array here
    NSDictionary * additionalData = @{
        @"an": accountName,
        @"ae": accountEmail,
        @"as": accountStartDate,
        @"ac": accountAttributes,
        @"un": userName,
        @"ue": userEmail,
        @"uc": userAttributes
    };
    NSMutableArray* userData = [[NSMutableArray alloc] init];
    [userData addObject: accountId];
    [userData addObject: userId];
    [userData addObject: additionalData];
    
    // JSON stringify array
    NSError *error = nil;
    NSData *json;
    NSString *jsonString;
    if ([NSJSONSerialization isValidJSONObject:userData]) {
        // Serialize the dictionary
        json = [NSJSONSerialization dataWithJSONObject:userData options:kNilOptions error:&error];
        
        // If no errors, get JSON string
        if (json != nil && error == nil) {
            jsonString = [[NSString alloc] initWithData:json encoding:NSUTF8StringEncoding];
            
            // adding user to tracker
            SPSubject *subject = [[SPSubject alloc] initWithPlatformContext:YES andGeoContext:NO];
            [subject setUserId:jsonString];
            
            [tracker_ setSubject:subject];
        }
    }
}
@end