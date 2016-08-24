//
//  EFTimePickerViewController.m
//  EFCircularSlider
//
//  Created by Eliot Fowler on 12/5/13.
//  Copyright (c) 2013 Eliot Fowler. All rights reserved.
//

#import "EFTimePickerViewController.h"
#import "EFCircularSlider.h"
#import "AKPickerView.h"

#import "TBMoods.h"
#import "NSAvailableMoods.h"
#import "MediaPlayer/MediaPlayer.h"
#import <MediaToolbox/MediaToolbox.h>
#import <QuartzCore/QuartzCore.h>

#define IDIOM    UI_USER_INTERFACE_IDIOM()
#define IPAD     UIUserInterfaceIdiomPad
NSString *flag;
float MuteValue;

@interface EFTimePickerViewController () <AKPickerViewDataSource, AKPickerViewDelegate>

{
    UIImageView * backgroundMoodView;
}
@property (nonatomic, strong) UIImageView * backgroundMoodView;;
@property (nonatomic, strong) AKPickerView *pickerView;
@property (nonatomic, strong) NSArray *titles;


@end

@implementation EFTimePickerViewController {
    EFCircularSlider* minuteSlider;
   // EFCircularSlider* hourSlider;
}
@synthesize backgroundMoodView;
AVAudioPlayer *player;
AVAudioPlayer *player2;
//MPMoviePlayerController *moviePlayer;

NSTimer *sliderTimer;


bool player1_running = false;
bool player2_running= false;

//bool notification = false;
bool played = false;

bool movie_player_value = false;

BOOL initialLoading =true;
MPMediaItem *playingItem;

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
    float vol = [[AVAudioSession sharedInstance] outputVolume];
    NSLog(@"output volume: %1.2f dB", 20.f*log10f(vol+FLT_MIN));
    
    UIImage *buttonImage = [UIImage imageNamed:@"Play"];
    [play_pause_Button setBackgroundImage:buttonImage forState:UIControlStateNormal];

    self.pickerView = [[AKPickerView alloc] initWithFrame:CGRectMake(0, 0, self.moodSelectionView.frame.size.width,  self.moodSelectionView.frame.size.height)];
    

    self.pickerView.delegate = self;
    self.pickerView.dataSource = self;
    self.pickerView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    [self.moodSelectionView addSubview:self.pickerView];
    
    self.pickerView.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:20];
    self.pickerView.highlightedFont = [UIFont fontWithName:@"HelveticaNeue" size:20];
    self.pickerView.interitemSpacing = 20.0;
    self.pickerView.fisheyeFactor = 0.001;
    self.pickerView.pickerViewStyle = AKPickerViewStyle3D;
    self.pickerView.maskDisabled = false;
    self.pickerView.textColor = [UIColor whiteColor];
     self.pickerView.highlightedTextColor = [UIColor whiteColor];
    
    self.titles = @[@"FOREST",
                    @"RAIN THUNDER",
                    @"BEATCH",
                    @"WATER FLOW"];
    
    [self.pickerView reloadData];
    [super viewDidLoad];
    
    CGRect  mixViewFrame = self.mixControllView.frame;
    int mixViewSliderFrame =  mixViewFrame.size.width-50;
    int mixsilderFrame =  mixViewFrame.size.width-85;
    int divitionFrame =  mixViewFrame.size.width-70;
    
    
    
    UIView * divitionLayer =[[UIView alloc] initWithFrame:CGRectMake((self.mixControllView.frame.size.width-divitionFrame)/2,(self.mixControllView.frame.size.height-divitionFrame)/2, divitionFrame, divitionFrame)];
    divitionLayer.layer.cornerRadius = divitionLayer.frame.size.width/2;
    [divitionLayer setBackgroundColor:[UIColor blackColor]];
    divitionLayer.layer.masksToBounds = YES;
    [self.mixControllView addSubview:divitionLayer];
    
    
    
    
    backgroundMoodView =[[UIImageView alloc] initWithFrame:CGRectMake((self.mixControllView.frame.size.width-mixsilderFrame)/2,(self.mixControllView.frame.size.height-mixsilderFrame)/2, mixsilderFrame, mixsilderFrame)];
    
    
    
    backgroundMoodView.layer.cornerRadius = backgroundMoodView.frame.size.width/2;
    [backgroundMoodView setBackgroundColor:[UIColor whiteColor]];
    backgroundMoodView.layer.masksToBounds = YES;
    [self.mixControllView addSubview:backgroundMoodView];
    
    
    
    
    CGRect minuteSliderFrame = CGRectMake((self.mixControllView.frame.size.width-mixViewSliderFrame)/2,(self.mixControllView.frame.size.height-mixViewSliderFrame)/2, mixViewSliderFrame, mixViewSliderFrame);
    minuteSlider = [[EFCircularSlider alloc] initWithFrame:minuteSliderFrame];
    minuteSlider.unfilledColor = [UIColor colorWithRed:25/255.0f green:1/255.0f blue:75/255.0f alpha:1.0f];
    minuteSlider.filledColor = [UIColor colorWithRed:250/255.0f green:46/255.0f blue:255/255.0f alpha:1.0f];
  //  [minuteSlider setInnerMarki/Users/toobler/Projects/SongScapeWithotAnimation ngLabels:@[@"5", @"10", @"15", @"20", @"25", @"30", @"35", @"40", @"45", @"50", @"55", @"60"]];
    minuteSlider.labelFont = [UIFont systemFontOfSize:14.0f];
    minuteSlider.lineWidth = 4;
    minuteSlider.minimumValue = 0;
    minuteSlider.maximumValue = 100;
    minuteSlider.labelColor = [UIColor colorWithRed:76/255.0f green:111/255.0f blue:137/255.0f alpha:1.0f];
    minuteSlider.handleType = CircularSliderHandleTypeDoubleCircleWithOpenCenter;
    minuteSlider.handleColor = minuteSlider.filledColor;
    [self.mixControllView addSubview:minuteSlider];
    [minuteSlider addTarget:self action:@selector(minuteDidChange:) forControlEvents:UIControlEventValueChanged];
    
//    CGRect hourSliderFrame = CGRectMake((self.mixControllView.frame.size.width-150)/2,(self.mixControllView.frame.size.height-150)/2, 150, 150);
//    hourSlider = [[EFCircularSlider alloc] initWithFrame:hourSliderFrame];
//    hourSlider.unfilledColor = [UIColor colorWithRed:23/255.0f green:47/255.0f blue:70/255.0f alpha:1.0f];
//    hourSlider.filledColor = [UIColor colorWithRed:98/255.0f green:243/255.0f blue:252/255.0f alpha:1.0f];
//    //[hourSlider setInnerMarkingLabels:@[@"1", @"2", @"3", @"4", @"5", @"6", @"7", @"8", @"9", @"10", @"11", @"12"]];
//    hourSlider.labelFont = [UIFont systemFontOfSize:14.0f];
//    hourSlider.lineWidth = 14;
//    hourSlider.snapToLabels = NO;
//    hourSlider.minimumValue = 0;
//    hourSlider.maximumValue = 500;
//    hourSlider.labelColor = [UIColor colorWithRed:127/255.0f green:229/255.0f blue:255/255.0f alpha:1.0f];
//    hourSlider.handleType = CircularSliderHandleTypeBigCircle;
//    hourSlider.handleColor = hourSlider.filledColor;
//   [self.mixControllView addSubview:hourSlider];
//    [hourSlider addTarget:self action:@selector(hourDidChange:) forControlEvents:UIControlEventValueChanged];
    
//    *** To set MoodLabel While Loading from the TableView ***//
    
    TBMoods *MoodName=_selectedMood;
     //NSLog(@"%@", MoodName.moodDescription);
    //MixMoodLabel.text=MoodName.moodDescription;
    
    
//    *** To set MoodImage While Loading from the TableView ***//
    
    
    NSLog(@"%@", MoodName.selectedFlag);
    if ([MoodName.selectedFlag isEqualToString:@"0"]) {
      //  _pickerView.selectedItem =
        [backgroundMoodView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"Forest"]]];
        
        
    }else if ([MoodName.selectedFlag isEqualToString:@"1"])
        
    {
        [backgroundMoodView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"Rain"]]];
        
    }else if ([MoodName.selectedFlag isEqualToString:@"2"])
    {
        [backgroundMoodView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"Beach"]]];
    }
    
    else if ([MoodName.selectedFlag isEqualToString:@"3"])
    {
        [backgroundMoodView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"WaterFlow"]]];
        
        
    }
    
    
    flag=@"1";
    [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
    
    
    
    //***To set ThumbImage for Slider Button***//
    
    [ProgressBar setThumbImage: [UIImage imageNamed:@"slider-thumb"] forState:UIControlStateNormal];
    
    [super viewDidLoad];
   [self player2_play:nil];
    

    
}


-(void)minuteDidChange:(EFCircularSlider*)slider {
    
//    int newVal = (int)slider.currentValue;
//    player2.volume = newVal/10;
    
    MuteValue =slider.currentValue;
    player2.volume = slider.currentValue/10;
    

    
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
#pragma mark - AKPickerViewDataSource

- (NSUInteger)numberOfItemsInPickerView:(AKPickerView *)pickerView
{
    return [self.titles count];
}

/*
 * AKPickerView now support images!
 *
 * Please comment '-pickerView:titleForItem:' entirely
 * and uncomment '-pickerView:imageForItem:' to see how it works.
 *
 */

- (NSString *)pickerView:(AKPickerView *)pickerView titleForItem:(NSInteger)item
{
    return self.titles[item];
}

/*
 - (UIImage *)pickerView:(AKPickerView *)pickerView imageForItem:(NSInteger)item
 {
	return [UIImage imageNamed:self.titles[item]];
 }
 */

#pragma mark - AKPickerViewDelegate

- (void)pickerView:(AKPickerView *)pickerView didSelectItem:(NSInteger)item
{
    NSLog(@"%@", self.titles[item]);
    
    player2.volume = minuteSlider.currentValue/10;
    
    [self handlingMoods:item];
    
}


/*
 * Label Customization
 *
 * You can customize labels by their any properties (except font,)
 * and margin around text.
 * These methods are optional, and ignored when using images.
 *
 */

/*
 - (void)pickerView:(AKPickerView *)pickerView configureLabel:(UILabel *const)label forItem:(NSInteger)item
 {
	label.textColor = [UIColor lightGrayColor];
	label.highlightedTextColor = [UIColor whiteColor];
	label.backgroundColor = [UIColor colorWithHue:(float)item/(float)self.titles.count
 saturation:1.0
 brightness:1.0
 alpha:1.0];
 }
 */

/*
 - (CGSize)pickerView:(AKPickerView *)pickerView marginForItem:(NSInteger)item
 {
	return CGSizeMake(40, 20);
 }
 */

#pragma mark - UIScrollViewDelegate

/*
 * AKPickerViewDelegate inherits UIScrollViewDelegate.
 * You can use UIScrollViewDelegate methods
 * by simply setting pickerView's delegate.
 *
 */

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    // Too noisy...
    // NSLog(@"%f", scrollView.contentOffset.x);
}
-(void)viewDidAppear:(BOOL)animated{
    
    CGRect Frame;
    if (self.view.bounds.size.height<568) {
        Frame=CGRectMake(4, 220, 312, 202);
    }
    else{
        Frame=CGRectMake(4, 264, 312, 202);
        
    }
    
    [UIView animateWithDuration:.4 animations:^{
        [CtrlThirdView setFrame:Frame];
    } completion:^(BOOL finished) {
        
    }];
    
    
    if (initialLoading) {
        [self openfolder:nil];
        initialLoading = false;
    }
    
}
/**
 *  @author Rohith
 *  HANDLE THE PLAY FUNCTION FOR PLAYER1
 */


- (IBAction)play:(id)sender{
    /*
     * Checking there is any songs available in the device
     * Else alert there is no media files in device
     */
    
    if(playingItem){
        
        NSString *songTitle = [playingItem valueForProperty: MPMediaItemPropertyTitle];
        
        //NSString * artist  = [playingItem valueForProperty:MPMediaItemPropertyArtist];
        /*
         * Checking the player1 runnin status and if no status then create new player and assingn
         * new song else playing the song
         * Setting for on the paused condition the song length to be found.
         */
        if(!player1_running){
            
            player = [[AVAudioPlayer alloc] initWithContentsOfURL:[playingItem valueForProperty: MPMediaItemPropertyAssetURL] error:nil];
            
            
            player1_running =     true;
            
            
            /* Getting the current song duraion */
            
            ProgressBar.maximumValue = player.duration;
            [ProgressBar addTarget:self action:@selector(sliderChanged:) forControlEvents:UIControlEventValueChanged];
            [player prepareToPlay];
            
            
            [player setDelegate:self];
        }
        /* Setting the slider value changes by calling the update slider method */
        
        sliderTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(updateSlider) userInfo:nil repeats:YES];
        
        /*
         * Checking whether the player is already on or not by checking the "played"
         * If the player is on then setting the "played" value to false getting the
         * Volumeslider value and pausing the player.
         * Else player is set to play the song.
         */
        
        if(played){
            played = false;
            UIImage *buttonImage = [UIImage imageNamed:@"Play"];
            [play_pause_Button setBackgroundImage:buttonImage forState:UIControlStateNormal];
            [player setCurrentTime:ProgressBar.value];
            [player pause];
            [player prepareToPlay];
            
        }else{
            played = true;
            UIImage *buttonImage = [UIImage imageNamed:@"Pause"];
            [play_pause_Button setBackgroundImage:buttonImage forState:UIControlStateNormal];
            [player setCurrentTime:ProgressBar.value];
            [player play];
        }

        
        /*
         * Calculating the time of the song currently playing and showing the increment
         * value according to the player play the song.
         */
        float cTime =ProgressBar.maximumValue;
        int minutes = floor(cTime/60);
        int seconds = trunc(cTime - minutes * 60);
        [endText setText:[NSString stringWithFormat:@"%d:%02d", minutes, seconds]];
        
        
        /*
         * Setting the song title and the songTitle name from the arrays and set text values
         */
        [MixMoodLabel setText:songTitle];
        
        if(songTitle == (NSString *)[NSNull null]){
            songTitle = @"Unknown Artist";
        }
        [MixMoodLabel setText:songTitle];
        
        if( ProgressBar.maximumValue == 0){
            [self nextButtonClick:sender];
        }
        
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"NO FILES"
                                                        message:@"No Files Found in the Music Library" delegate:self cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
    }
}




////...To Mute the Mixer Sound...//
//- (IBAction)MixerMute:(id)sender
//{
//    static BOOL muted = NO;
//    if (muted) {
//       // [mute1 setImage:[UIImage imageNamed:@"mix_mute@2x.png"] forState:UIControlStateNormal];
//        
//        player2.volume =MuteValue;
//        // [player2 setVolume:1.0];
//    } else {
//        [player2 setVolume:0.0];
//        //[mute1 setImage:[UIImage imageNamed:@"mix_mute.png"] forState:UIControlStateNormal];
//        
//    }
//    muted = !muted;
//}


/*
 *  @author Rohith
 *
 * HANDLE THE SLIDER CHANGES ACCORDING
 * THE PLAYER 1 PLAYS THE SONG
 */

- (void)updateSlider {
    if(playingItem){
        // Update the slider about the music time
        ProgressBar.value = player.currentTime;
        float cTime =player.currentTime;
        int minutes = floor(cTime/60);
        int seconds = trunc(cTime - minutes * 60);
        [startText setText:[NSString stringWithFormat:@"%d:%02d", minutes, seconds]];
    }
}

- (IBAction)sliderChanged:(UISlider *)sender {
    if(playingItem){
        // Fast skip the music when user scroll the UISlider
        [player stop];
        [player setCurrentTime:ProgressBar.value];
        [player prepareToPlay];
        if(played){
            [player play];
        }
    }
}


- (IBAction)Backward:(id)sender {
    if(playingItem){
        [self fetchNear:false];
        sliderTimer = 0;
        ProgressBar.value = 0;
        player1_running =false;
        [player stop];
        [self play:nil];
    }
}

- (IBAction)nextButtonClick:(id)sender {
    
    if(playingItem){
        
        [self fetchNear:true];
        sliderTimer = 0;
        ProgressBar.value = 0;
        player1_running =false;
        [player stop];
        [self play:nil];
    }
}



- (void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)play successfully:(BOOL)flag {
    /*
     * If player2 is finished then
     * it is called again for playing for
     * didnt stop the mood music
     */
    if(playingItem){
        /*
         * Checking the player stopped is player1 or player2
         */
        if( play == player2){
            [player2 play];
        }else{
            // For player1
            // Setting the Slider variation after the player stops
            if (flag) {
                [sliderTimer invalidate];
            }
            // If the last listed file get played and completed
            // Again reversed to the first file in the list
            // If the music completed it goes to 1st song
            
            [self fetchNear:true];
            player1_running = false;
            played = false;
            ProgressBar.value = 0;
            [self play:nil];
        }}
}


- (IBAction)player2_play:(id)sender {
    
    if(player2_running){
        
        
        // For pausing the player2 setting up the teststring to nil, movie_player_value to false
        // And setting the buttons to the previous style.
        
        movie_player_value = false;
        //[moviePlayer.view setFrame:CGRectMake(0, 0, 0, 0)];
        
        // Setting the movieplayer content to nil and stops the movie player
        //moviePlayer.contentURL = nil;
        //[moviePlayer stop];
        
        // stopping the mood song player
        [player2 stop];
        
        // setting the playing text and image to nil
        [playingimage setImage:nil];
        [playingText setText:nil ];
        player2_running = false;
    }
    
    
    if(!player2_running)
    {
        
        
        // For starting or restarting the  mood player , stopping the movie player for start another video
        
        player2_running = true;
        //[moviePlayer stop];
        
        {
            
            // else Setting the images and texts for rain
            
            [playingimage setImage:[UIImage imageNamed:@"rain"]];
            [playingText setText:[NSString stringWithFormat:@"Rain & Thunder"] ];
            [buttonRain setBackgroundImage:[UIImage imageNamed:@"rain_pause"] forState:UIControlStateNormal];
            [buttonnature setBackgroundImage:[UIImage imageNamed:@"forest"] forState:UIControlStateNormal];
            [buttonSea setBackgroundImage:[UIImage imageNamed:@"sea"] forState:UIControlStateNormal];
        }
        
        
        // Setting the file path which the movie player file to be stored
        //Starting the movie player by setting the new resource
        // Starting the mood player also by setting the new resource
        
        
        NSURL *songURL = [NSURL fileURLWithPath:[[NSBundle mainBundle] pathForResource:_selectedMood.moodSound  ofType:@"mp3"]];
        player2 = [[AVAudioPlayer alloc] initWithContentsOfURL:songURL error:nil];
        [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback error:nil];
        [[AVAudioSession sharedInstance] setActive: YES error: nil];
        [[UIApplication sharedApplication] beginReceivingRemoteControlEvents];
        player2.delegate=self;
        
        player2.volume = minuteSlider.currentValue;
        [player2 play];
    }
    
}




- (IBAction)openfolder:(id)sender {
    MPMediaPickerController *picker = [[MPMediaPickerController alloc] initWithMediaTypes:MPMediaTypeAnyAudio];
    [picker setDelegate:self];
    [picker setAllowsPickingMultipleItems:NO];
    [picker setPrompt:NSLocalizedString(@"Add songs to play","Prompt in media item picker")];
    
    @try {
        [picker loadView]; // Will throw an exception in iOS simulator
        [self presentViewController:picker animated:YES completion:nil];
    }
    @catch (NSException *exception) {
        [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Oops!",@"Error title")
                                    message:NSLocalizedString(@"The music library is not available.",@"Error message when MPMediaPickerController fails to load")
                                   delegate:nil
                          cancelButtonTitle:@"OK"
                          otherButtonTitles:nil] show];
    }
}

/**
 *  @author Rohith
 *  AUTOMATICALLY HANDLED WHEN WE PICK ANY FILE FROM THE LIST
 *
 * It will handle when ever any of the songs from the list selected
 * and it will checkout the positon and array index of the song selected
 */

- (void)mediaPicker: (MPMediaPickerController *)mediaPicker didPickMediaItems:
(MPMediaItemCollection *)mediaItemCollection
{
    [self dismissViewControllerAnimated:YES completion:nil];
    playingItem = [[mediaItemCollection items] objectAtIndex:0];
    
    /*
     * Setting the player1 running status to false and stop the player1
     */
    
    player1_running =false;
    [player stop];
    sliderTimer = 0;
    ProgressBar.value = 0;
    played = false;
    [self play:nil];
    
}

/*
 * Handling the Cancellation the media list
 */
- (void) mediaPickerDidCancel: (MPMediaPickerController *) mediaPicker
{
    [self dismissViewControllerAnimated:YES completion:nil];
}



-(void)fetchNear:(BOOL)next{
    if(playingItem)
    {
        MPMediaQuery *everything = [[MPMediaQuery alloc] init];
        NSArray *itemsFromGenericQuery = [everything items];
        everything = nil;
        if ([itemsFromGenericQuery containsObject:playingItem]) {
            int  i =  [itemsFromGenericQuery indexOfObject:playingItem];
            if(next){
                // fetch Next Song
                if((i+1)<[itemsFromGenericQuery count]){
                    playingItem =[itemsFromGenericQuery objectAtIndex:i+1];
                }else if([itemsFromGenericQuery count]>0){
                    playingItem =[itemsFromGenericQuery objectAtIndex:0];
                }
                else{
                    playingItem =nil;
                }
            }else{
                // fetch Previous Song
                if((i-1)>=0){
                    playingItem =[itemsFromGenericQuery objectAtIndex:i-1];
                }else if([itemsFromGenericQuery count]>0){
                    playingItem =[itemsFromGenericQuery objectAtIndex:[itemsFromGenericQuery count]-1];
                }
                else{
                    playingItem =nil;
                }
            }
        }
    }
    
    
    if(played){
        played = false;
    }else{
        played = true;
    }
}


-(void)handlingMoods :(NSInteger)handlingtag
{
    
    _selectedMood=[[[NSAvailableMoods getInstance]getAvailableMoods]objectAtIndex:handlingtag];
    NSLog(@"%@", _selectedMood);
    [self player2_play:nil];
    
    if ([[NSString stringWithFormat:@"%ld",(long)handlingtag]isEqualToString:@"0"]) {
        
        [backgroundMoodView setImage:[UIImage imageNamed:@"Forest"]];
    }
    if ([[NSString stringWithFormat:@"%ld",(long)handlingtag]isEqualToString:@"1"]) {
        
           [backgroundMoodView setImage:[UIImage imageNamed:@"Rain"]];
    }
    if ([[NSString stringWithFormat:@"%ld",(long)handlingtag]isEqualToString:@"2"]) {
        
            [backgroundMoodView setImage:[UIImage imageNamed:@"Beach"]];
    }
    if ([[NSString stringWithFormat:@"%ld",(long)handlingtag]isEqualToString:@"3"]) {
        
         [backgroundMoodView setImage:[UIImage imageNamed:@"waterflow.png"]];
    }
}

-(IBAction)backButtonPressed:(id)sender
{
    //[self.navigationController popViewControllerAnimated:YES];
    [self.navigationController popToRootViewControllerAnimated:YES];



}


@end
