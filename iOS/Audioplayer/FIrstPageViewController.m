//
//  FIrstPageViewController.m
//  Audioplayer
//
//  Created by Toobler on 01/04/14.
//  Copyright (c) 2014 Toobler. All rights reserved.
//

#import "FIrstPageViewController.h"
#import "EFTimePickerViewController.h"
#import "NSAvailableMoods.h"
#import "TBMoods.h"
#import "MoodTableViewCell.h"

@interface FIrstPageViewController ()

@end

@implementation FIrstPageViewController

static int selectedIndex;

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
	// Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([segue.identifier isEqualToString:@"showView"]) {
        EFTimePickerViewController *destViewController = segue.destinationViewController;
        [destViewController setSelectedMood:[[[NSAvailableMoods getInstance]getAvailableMoods]objectAtIndex:selectedIndex]];
    }
}

#pragma mark ---------- TABLE METHODS --------
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return [[[NSAvailableMoods getInstance]getAvailableMoods] count];
}
-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    TBMoods *mood = [[[NSAvailableMoods getInstance]getAvailableMoods]objectAtIndex:indexPath.row];
    MoodTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"moodCell" forIndexPath:indexPath];
    cell.moodDescription.text=mood.moodDescription;
    cell.moodTitle.text = mood.moodName;
    cell.moodImageView.image =[UIImage imageNamed:mood.moodImage];
    return cell;
    
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    selectedIndex = indexPath.row;
    [self performSegueWithIdentifier:@"showView" sender:self];
}


@end
