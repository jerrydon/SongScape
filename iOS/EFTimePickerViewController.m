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
#define IPAD     UIUserInterfaceIdiomPadƒ
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
NSTimer *sliderTimer;


bool player1_running = false;
bool player2_running= false;
bool played = false;
bool movie_player_value = false;
BOOL initialLoading =true;
MPMediaItem *playingItem;
BOOL repeatOne = false;
UILabel * mixCountLabel;
BOOL abc = false;



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
    [[UIApplication sharedApplication] beginReceivingRemoteControlEvents];
    
    
    
    [play_pause_Button setImage:[UIImage imageNamed:@"Play"] forState:UIControlStateNormal];
    //To set repetButtonImage Initial load:
    UIImage *repetButtonImage = [UIImage imageNamed:@"repet"];
    [repeatButton setBackgroundImage:repetButtonImage forState:UIControlStateNormal];
    //To set shuffleButtonImage Initial load:
    UIImage *shuffleButtonImage = [UIImage imageNamed:@"shuffle"];
    [shuffleButton setBackgroundImage:shuffleButtonImage forState:UIControlStateNormal];


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
                    @"BEACH",
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
    
    //For Adding Mix BackGround Image
    backgroundMoodView =[[UIImageView alloc] initWithFrame:CGRectMake((self.mixControllView.frame.size.width-mixsilderFrame)/2,(self.mixControllView.frame.size.height-mixsilderFrame)/2, mixsilderFrame, mixsilderFrame)];
    
    backgroundMoodView.layer.cornerRadius = backgroundMoodView.frame.size.width/2;
    [backgroundMoodView setBackgroundColor:[UIColor whiteColor]];
    backgroundMoodView.layer.masksToBounds = YES;
    [self.mixControllView addSubview:backgroundMoodView];
    
    //For Adding Label inside Mix BackGround Image:
    mixCountLabel = [[UILabel alloc]initWithFrame: CGRectMake((self.mixControllView.frame.size.width-mixsilderFrame)/2,(self.mixControllView.frame.size.height-mixsilderFrame)/2, mixsilderFrame, mixsilderFrame)];
    
    mixCountLabel.text = @"100%";
    mixCountLabel.textColor = [UIColor whiteColor];
    mixCountLabel.textAlignment = NSTextAlignmentCenter;
    mixCountLabel.font = [UIFont systemFontOfSize:50];
    
    mixCountLabel.layer.cornerRadius = backgroundMoodView.frame.size.width/2;
    [mixCountLabel setBackgroundColor:[UIColor clearColor]];
    mixCountLabel.layer.masksToBounds = YES;
    [self.mixControllView addSubview:mixCountLabel];

    
    
    
    CGRect minuteSliderFrame = CGRectMake((self.mixControllView.frame.size.width-mixViewSliderFrame)/2,(self.mixControllView.frame.size.height-mixViewSliderFrame)/2, mixViewSliderFrame, mixViewSliderFrame);
    minuteSlider = [[EFCircularSlider alloc] initWithFrame:minuteSliderFrame];
    minuteSlider.unfilledColor = [UIColor colorWithRed:25/255.0f green:1/255.0f blue:75/255.0f alpha:1.0f];
    minuteSlider.filledColor = [UIColor colorWithRed:250/255.0f green:46/255.0f blue:255/255.0f alpha:1.0f];
    minuteSlider.labelFont = [UIFont systemFontOfSize:14.0f];
    minuteSlider.lineWidth = 4;
    minuteSlider.minimumValue = 0;
    minuteSlider.maximumValue = 10;
    minuteSlider.currentValue = 2;
    minuteSlider.labelColor = [UIColor colorWithRed:76/255.0f green:111/255.0f blue:137/255.0f alpha:1.0f];
    minuteSlider.handleType = CircularSliderHandleTypeDoubleCircleWithOpenCenter;
    minuteSlider.handleColor = minuteSlider.filledColor;
    [self.mixControllView addSubview:minuteSlider];
    [minuteSlider addTarget:self action:@selector(minuteDidChange:) forControlEvents:UIControlEventValueChanged];
    

    
    
//    *** To set MoodLabel While Loading Initially
       _selectedMood=[[[NSAvailableMoods getInstance]getAvailableMoods]objectAtIndex:0];
    
//    *** To set MoodImage While Loading from the TableView ***//
    [backgroundMoodView setImage:[UIImage imageNamed:@"Forest"]];
    flag=@"1";
    [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
    
    
    //***To set ThumbImage for Slider Button***//
    [ProgressBar setThumbImage: [UIImage imageNamed:@"slider-thumb"] forState:UIControlStateNormal];
    
    [super viewDidLoad];
   [self player2_play:nil];
    
  

    
}


////*******Jithu
//- (IBAction)volumeDidChange:(UISlider *)slider {
//    //Handle the slider movement
//    [player setVolume:[slider value]];
//}
//
//- (IBAction)togglePlayingState:(id)button {
//    //Handle the button pressing
//    [self togglePlayPause];
//}
////**********Jithu



- (void)endInterruptionWithFlags:(NSUInteger)flags {
    // Validate if there are flags available.
    if (flags) {
        // Validate if the audio session is active and immediately ready to be used.
        if (AVAudioSessionInterruptionFlags_ShouldResume) {
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 1), dispatch_get_main_queue(), ^{
                // Resume playing the audio.
            });
        }
    }
}


-(void)minuteDidChange:(EFCircularSlider*)slider {
    
//    int newVal = (int)slider.currentValue;
//    player2.volume = newVal/10;
    
  
    player2.volume = slider.currentValue/10;
    NSLog(@"%f",player2.volume);
    
    int abc = player2.volume*100;
    NSLog(@"%d", abc);
    
    mixCountLabel.text = [NSString stringWithFormat:@"%d %@", abc, @"%Mix"];
    NSLog(@"%@",mixCountLabel);
    
    

    
}
//MPMediaItem item = [arrAnand objectAtIndex:i];
//NSURL *url = [item valueForProperty:MPMediaItemPropertyAssetURL];
//AVPlayerItem playerItem = [AVPlayerItem playerItemWithURL:url];
//[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(itemDidFinishPlaying) name:AVPlayerItemDidPlayToEndTimeNotification object:playerItem];
//AVPlayer* player = [[[AVPlayer alloc] initWithPlayerItem:playerItem] autorelease]; [player play];
//} -(void)itemDidFinishPlaying { // Will be called when AVPlayer finishes playing playerItem }` –


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
    
    
    //Once the view has loaded then we can register to begin recieving controls and we can become the first responder
    [[UIApplication sharedApplication] beginReceivingRemoteControlEvents];
    [self becomeFirstResponder];
    
    //To open Songs folder on Initial Loading.
    if (initialLoading) {
        [self openfolder:nil];
        initialLoading = false;
    }
    
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    //End recieving events
    [[UIApplication sharedApplication] endReceivingRemoteControlEvents];
    [self resignFirstResponder];
}

////****Jithu
//- (void)playAudio {
//    //Play the audio and set the button to represent the audio is playing
//    [player play];
//    [player2 play];
//    [play_pause_Button setImage:[UIImage imageNamed:@"Pause"] forState:UIControlStateNormal];
//    [play_pause_Button setTitle:@"Pause" forState:UIControlStateNormal];
//    
//}
//
//- (void)pauseAudio {
//    //Pause the audio and set the button to represent the audio is paused
//    [player pause];
//    //[player2 pause];
//    [play_pause_Button setImage:[UIImage imageNamed:@"Play"] forState:UIControlStateNormal];
//    [play_pause_Button setTitle:@"Play" forState:UIControlStateNormal];
//    
//}
//
//- (void)togglePlayPause {
//    //Toggle if the music is playing or paused
//    if (!player.playing) {
//        [self playAudio];
//        
//    } else if (player.playing) {
//        [self pauseAudio];
//        
//    }
//}

//Make sure we can recieve remote control events
- (BOOL)canBecomeFirstResponder {
    return YES;
}

- (void)remoteControlReceivedWithEvent:(UIEvent *)event {
    //if it is a remote control event handle it correctly
    NSLog(@"%@", event);
    if (event.type == UIEventTypeRemoteControl) {
        
        if (event.subtype == UIEventSubtypeRemoteControlPlay) {
            
            [self play:nil];
            
        }
        else if (event.subtype == UIEventSubtypeRemoteControlPause) {
            abc = true;
            [self play:nil];
            abc = false;
            
//            [self togglePlayPause];

            
            
        }
        else if (event.subtype == UIEventSubtypeRemoteControlNextTrack){
            
            [self nextButtonClick:nil];
        }else if (event.subtype == UIEventSubtypeRemoteControlPreviousTrack){
            
            [self Backward:nil];
        }
    }
}
//********Jithu

/**
 *  @author Rohith
 *  HANDLE THE PLAY FUNCTION FOR PLAYER1
 */

- (IBAction)play:(id)sender{
    /*
     * Checking there is any songs available in the device
     * Else alert there is no media files in device
     */
    
    //******Jithu
    
    NSError *error;
    
    if(!playingItem){
        
        NSLog(@"%@", [error localizedDescription]);
        
         [play_pause_Button setEnabled:NO];
        
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"NO FILES"
                                                        message:@"No Files Found in the Music Library" delegate:self cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];

    } else if (played == false){
        
        //Declare the audio file location and settup the player
        player = [[AVAudioPlayer alloc] initWithContentsOfURL:[playingItem valueForProperty: MPMediaItemPropertyAssetURL] error:nil];
        
        NSLog(@"%@", player);
        
        
        //Make sure the system follows our playback status
        [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback error:nil];
        [[AVAudioSession sharedInstance] setActive: YES error: nil];
        [[UIApplication sharedApplication] beginReceivingRemoteControlEvents];

        
        
//    //NSString *category = supportsBackgroundOperation ? AVAudioSessionCategoryPlayback : AVAudioSessionCategoryAmbient;
//    [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback withOptions:AVAudioSessionCategoryOptionMixWithOthers error:nil];
        
        
        /* Getting the current song duraion */
        
        ProgressBar.maximumValue = player.duration;
        [ProgressBar addTarget:self action:@selector(sliderChanged:) forControlEvents:UIControlEventValueChanged];
        [player prepareToPlay];
        
        /* Setting the slider value changes by calling the update slider method */
        sliderTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(updateSlider) userInfo:nil repeats:YES];

        NSString *songTitle = [playingItem valueForProperty: MPMediaItemPropertyTitle];
        
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
        
        /*
         * Calculating the time of the song currently playing and showing the increment
         * value according to the player play the song.
         */
        float cTime =ProgressBar.maximumValue;
        int minutes = floor(cTime/60);
        int seconds = trunc(cTime - minutes * 60);
        [endText setText:[NSString stringWithFormat:@"%d:%02d", minutes, seconds]];


                    played = true;
                    [play_pause_Button setImage:[UIImage imageNamed:@"Pause"] forState:UIControlStateNormal];
                    [player setCurrentTime:ProgressBar.value];
                    [player play];
                    [player2 play];
                    [player prepareToPlay];
                    [player setDelegate:self];
//                }


        //Load the audio into memory
        //[player play];
    }else {
        played = false;
        if (abc == false){
            [play_pause_Button setImage:[UIImage imageNamed:@"Play"] forState:UIControlStateNormal];
            [player setCurrentTime:ProgressBar.value];
            [player pause];
            [player2 pause];
        }else{
            [play_pause_Button setImage:[UIImage imageNamed:@"Play"] forState:UIControlStateNormal];
            [player setCurrentTime:ProgressBar.value];
            [player pause];
//            [player2 pause];
            

        }
        
        [player prepareToPlay];
        [player setDelegate:self];

    }
    
}


// To repeat same song:
-(IBAction)repeatSong:(id)sender{
   
    if (repeatOne == true){
        player.numberOfLoops = 0;
        
        UIImage *repetButtonImage = [UIImage imageNamed:@"repet"];
        [repeatButton setBackgroundImage:repetButtonImage forState:UIControlStateNormal];
        
        repeatOne = false;
    
    }else{
        player.numberOfLoops = -1;
        
        UIImage *repetButtonImage = [UIImage imageNamed:@"repetSelect"];
        [repeatButton setBackgroundImage:repetButtonImage forState:UIControlStateNormal];
        repeatOne = true;
    }
    
}


//To shuffle the Songs.

-(IBAction)suffleSongs:(id)sender{
    
    
    int randomNumber = arc4random() % 8 + 1;
    
//    player = [[AVAudioPlayer alloc] initWithContentsOfURL:[playingItem valueForProperty: MPMediaItemPropertyAssetURL] error:nil];
    
    
    
    
    NSURL *soundURL = [NSURL fileURLWithPath:[[NSBundle mainBundle]pathForResource:[NSString stringWithFormat:@"Sound%02d", randomNumber] ofType:@"mp3"]];
    
    NSLog(@"%@", soundURL);
    
    player = [[AVAudioPlayer alloc] initWithContentsOfURL:soundURL error:nil];
    
    [player prepareToPlay];
    [player play];
    
    
    NSLog(@"randomNumber is %d", randomNumber);
    NSLog(@"tmpFilename is %@", soundURL);
    
}



//-(void)itemDidFinishPlaying
//{
//    
//    MPMediaQuery *everything = [[MPMediaQuery alloc] init];
////    MPMediaItem * abc  = [everything items];
////    NSArray *itemsFromGenericQuery = [everything items];
////    everything = nil;
////    if ([itemsFromGenericQuery containsObject:playingItem]) {
////        int  i =  [itemsFromGenericQuery indexOfObject:playingItem];
////    }
//
//    
//    
////    MPMediaItem *item = [itemsFromGenericQuery objectAtIndex:everything];
//    
//    MPMediaItem *abc = [everything items];
//    
//    NSURL *url = [abc valueForProperty:MPMediaItemPropertyAssetURL];
//    
//    if(player)
//    {
//        player = nil;
//    }
//    player = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:nil];
//    [player play];
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
        
        
        // stopping the mood song player
        [player2 stop];
        
        // setting the playing text and image to nil
        [playingimage setImage:nil];
        [playingText setText:nil ];
        player2_running = false;
    }
    
    
    if(!player2_running)
    {
        
        
        // For starting or restarting the  mood player
        
        player2_running = true;
        
        // Setting the file path which the movie player file to be stored
        //Starting the movie player by setting the new resource
        // Starting the mood player also by setting the new resource
        
        
        NSURL *songURL = [NSURL fileURLWithPath:[[NSBundle mainBundle] pathForResource:_selectedMood.moodSound  ofType:@"mp3"]];
        player2 = [[AVAudioPlayer alloc] initWithContentsOfURL:songURL error:nil];
        [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback error:nil];
        [[AVAudioSession sharedInstance] setActive: YES error: nil];
        [[UIApplication sharedApplication] beginReceivingRemoteControlEvents];
        player2.delegate=self;
        
        player2.volume = minuteSlider.currentValue/10;
        NSLog(@"%f",player2.volume);
        int abc = player2.volume*100;
        NSLog(@"%d", abc);
        mixCountLabel.text = [NSString stringWithFormat:@"%d %@", abc, @"%Mix"];
        NSLog(@"%@",mixCountLabel);

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
        
         [backgroundMoodView setImage:[UIImage imageNamed:@"WaterFlow"]];
    }
}


@end
