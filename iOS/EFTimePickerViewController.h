//
//  EFTimePickerViewController.h
//  EFCircularSlider
//
//  Created by Eliot Fowler on 12/5/13.
//  Copyright (c) 2013 Eliot Fowler. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TBMoods.h"
#import <AVFoundation/AVFoundation.h>
#import <MediaPlayer/MediaPlayer.h>
#import "NSAvailableMoods.h"

@interface EFTimePickerViewController : UIViewController<AVAudioPlayerDelegate,MPMediaPickerControllerDelegate>
{
   
    
   // __weak IBOutlet UISlider *volumeSlider;
    
    __weak IBOutlet UIImageView *LogoImage;
    
    __weak IBOutlet UIImageView *BGImage;
    
    __weak IBOutlet UIImageView *music;
    
    __weak IBOutlet UIImageView *mixer;
    
    __weak IBOutlet UIButton *mute;
    
    __weak IBOutlet UIButton *mute1;
    
    __weak IBOutlet UILabel *MixMoodLabel;
    
    __weak IBOutlet UIView *AddButtonView;
    
    __weak IBOutlet UIButton *AddButton;
    
    __weak IBOutlet UIView *CtrlFirstView;
    
    __weak IBOutlet UIView *CtrlSecondView;
    
    __weak IBOutlet UIView *CtrlThirdView;
    
    __weak IBOutlet UIView *aboveView;
    
  //  __weak IBOutlet UISlider *volumeSlider2;
    
    __weak IBOutlet UISlider *ProgressBar;
    
    __weak IBOutlet UILabel *endText;
    
    __weak IBOutlet UILabel *startText;
    
    __weak IBOutlet UILabel *Textdata;
    
    __weak IBOutlet UIImageView *playingimage;
    
    __weak IBOutlet UILabel *playingText;
    
    __weak IBOutlet UIButton *buttonnature;
    
    __weak IBOutlet UIButton *buttonRain;
    
    __weak IBOutlet UIButton *buttonSea;
    
    __weak IBOutlet UIButton *buttonCrowd;
    
    __weak IBOutlet UIButton *buttonForward;
    
    __weak IBOutlet UIButton *buttonBackward;
    
    __weak IBOutlet UILabel *playing_label;
    
    __weak IBOutlet UILabel *artist_Label;
    
    __weak IBOutlet UIButton *play_pause_Button;
    
    __weak IBOutlet UIView *player_View;
    
    __weak IBOutlet UIView *controllView;
    
    __weak IBOutlet UIButton *animationButton;
    
     __weak IBOutlet UIButton *shuffleButton;
    
     __weak IBOutlet UIButton *repeatButton;
    
     __weak IBOutlet UIButton *favouriteButton;
    
    // __weak IBOutlet UILabel *controllLabel;
    
    //__weak IBOutlet UILabel *ScrollLabel;
    
    IBOutlet UIView *tapRecoganiserView;
    
    IBOutlet UIView *blerdView;
    
    IBOutlet UIScrollView *moodScrollView;
    
    UIImageView *logo;
    
}

@property (weak, nonatomic) IBOutlet UILabel *timeLabel;
@property (nonatomic,retain)  TBMoods* selectedMood;
@property(nonatomic,retain)IBOutlet UIView *  mixControllView;
@property(nonatomic,retain)IBOutlet UIView *  moodSelectionView;

- (IBAction)play:(id)sender;
//- (IBAction)pause:(id)sender;
- (IBAction)volumeChange:(id)sender;
//- (IBAction)pause2:(id)sender;
- (IBAction)volumeChange2:(id)sender;
- (IBAction)openfolder:(id)sender;
//- (IBAction)stop:(id)sender;
- (IBAction)Backward:(id)sender;
- (IBAction)nextButtonClick:(id)sender;
-(IBAction)backButtonPressed:(id)sender;
-(IBAction)animationButton:(id)sender;



@end
