//
//  ViewController.m
//  jokeGenerator
//
//  Created by Sparked.
//  This is a sample view controller file provided to guide the user through
//  the installation of Sparked Tracker.
//

#import "ViewController.h"
#import "SparkedTracker.h"
#import "SPTracker.h"
#import "SPEvent.h"
#import "SPSelfDescribingJson.h"


@interface ViewController ()

@end

@implementation ViewController {
    SPTracker *       _tracker;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    [self setup];
    
    /* Example tracking a page view: Insert the right url or screen name here instead of "test.foo.com" */
    [self trackPageView:_tracker url:@"test.foo.com"];
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)JokeButton:(id)sender {
    
    NSString *fileContents = [[NSBundle mainBundle] pathForResource:@"jokeList" ofType:@"plist"];
    NSDictionary *wordDIC = [[NSDictionary alloc] initWithContentsOfFile:fileContents];
    
    NSMutableArray *items = [wordDIC valueForKey:@"Jokes"];
    int RandomJoke = arc4random() % [items count];
    NSString *Word = [items objectAtIndex:RandomJoke];
    
    [self.Label setText:[[NSString alloc] initWithFormat:@"%@", Word]];
    
    /* Example tracking an unstructured event: Insert your event type and attributes here */
    NSString * eventType = @"Watched video";
    NSDictionary * attributes = @{@"length":@"100", @"id":@"mov9834a"};
    
    [self trackUnStructuredEvent:_tracker eventType:eventType attributes:attributes];
}

- (void) setup {
    NSString * url = @"snowplow.sparked.com";
    /* Insert appId here */
    _tracker = [SparkedTracker createTracker:@"appId" url:url];
    /* Insert your logged in account id here (in quotes) */
    NSString * accountId = @"nobody";
    /* Set the start date of the account in YYYY-MM-DD format */
    NSString * accountStartDate = @"2016-01-01";
    /* Account name */
    NSString * accountName = @"John Q. Nobody";
    /* Accout email */
    NSString * accountEmail = @"nobody@devnull";
    /* Insert static account attributes here. */
    NSDictionary * accountAttributes = @{};
    /* If your business has mutliple users per account, set the user id here */
    NSString * userId = @"";
    /* User name */
    NSString * userName = @"";
    /* User email */
    NSString * userEmail = @"";
    /* Insert static user attributes here. */
    NSDictionary * userAttributes = @{};
    
    [SparkedTracker updateUser:_tracker
                     accountId:accountId
                   accountName:accountName
                  accountEmail:accountEmail
              accountStartDate:accountStartDate
             accountAttributes:accountAttributes
                        userId:userId
                      userName:userName
                     userEmail:userEmail
                userAttributes:userAttributes
     ];
}

- (void) trackPageView:(SPTracker *)tracker_ url:(NSString *)url {
    SPPageView *event = [SPPageView build:^(id<SPPageViewBuilder> builder) {
        [builder setPageUrl:url];
    }];
    [tracker_ trackPageViewEvent:event];
}

- (void) trackUnStructuredEvent:(SPTracker *)tracker_ eventType:(NSString *)eventType attributes:(NSDictionary *)attributes{
    // create the event data dictionary
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    data[@"evt"] = eventType;
    data[@"attributes"] = attributes;
    
    SPSelfDescribingJson * sdj = [[SPSelfDescribingJson alloc] initWithSchema:@"."
                                                                      andData:data];
    SPUnstructured *event = [SPUnstructured build:^(id<SPUnstructuredBuilder> builder) {
        [builder setEventData:sdj];
    }];
    [tracker_ trackUnstructuredEvent:event];
    
}


@end
