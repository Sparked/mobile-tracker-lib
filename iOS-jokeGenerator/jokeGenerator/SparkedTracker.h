//
//  SparkedTracker.h
//
//  Created by Sparked.
//  Please add this file to your app folder in your app workspace
//


#import <Foundation/Foundation.h>

@class SPTracker;
@class SPEmitter;

@interface SparkedTracker : NSObject

/**
 * Setup tracker object
 */

+ (SPTracker *) createTracker:(NSString *)userid_ url:(NSString *)url_;

/**
 * Update the user info in the tracker with the user data passed in
 */
+ (void) updateUser:(SPTracker *)tracker_
          accountId:(NSString *) accountId
        accountName:(NSString *) accountName
       accountEmail:(NSString *) accountEmail
   accountStartDate:(NSString *) accountStartDate
  accountAttributes:(NSDictionary *) accountAttributes
             userId:(NSString *) userId
           userName:(NSString *) userName
          userEmail:(NSString *) userEmail
     userAttributes:(NSDictionary *) userAttributes;

@end

