//
//  InitialViewController.m
//  Audioplayer
//
//  Created by Mzalih on 21/10/14.
//  Copyright (c) 2014 Toobler. All rights reserved.
//

#import "InitialViewController.h"

@interface InitialViewController ()

@end

@implementation InitialViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    //[[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"hasSeenTutorial"];
    
    // Do any additional setup after loading the view.
}
-(void)viewDidAppear:(BOOL)animated{
//    if(![[NSUserDefaults standardUserDefaults] boolForKey:@"hasSeenTutorial"]){
//        [self displayTutorial];
//    }else{
        [self startApp];
//    }

}
//-(void)displayTutorial{
//    [self performSegueWithIdentifier:@"startTutorial" sender:self];
//}

-(void)startApp{
    [self performSegueWithIdentifier:@"startApp" sender:self];
}
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
